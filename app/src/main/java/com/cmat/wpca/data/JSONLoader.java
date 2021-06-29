package com.cmat.wpca.data;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.cmat.wpca.data.entry.IEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A class to assist loading JSON files from the OS correct storage locations <br /><br />
 * The point of this class is to prevent direct access to files and file names which will
 * eliminate a large source of unnecessary errors, determines file names based on entry names in a consistent manner
 */
@SuppressWarnings("unused")
public class JSONLoader {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String fileDirectory; // The directory within storage that this Loader loads from/saves to
    private final StorageType storageType;

    private final boolean noDirectory;

    /**
     * A class to assist loading JSON files from the OS correct storage locations
     * @param fileDirectory The directory within storage that this Loader loads from/saves to, set to null or "" to use the default directory
     * @param storageType Internal or external storage
     */
    public JSONLoader(String fileDirectory, StorageType storageType) {
        this.fileDirectory = fileDirectory;
        this.storageType = storageType;
        noDirectory = (fileDirectory == null || fileDirectory.equals(""));
    }

    /**
     * Attempts to read an entry from the Loaders storage location, will try to return a blank entry if none exists
     * , may return bad data if you try to read a different kind of entry to the one that was stored so make sure to
     * not do that
     * @param context context associated with the app, use getContext()
     * @param entryName the name of the entry to attempt to read
     * @param tClass the class of the entry, must be of type T
     * @param <T> the type of the entry to read, must match the class
     * @return The entry read from the file, or a blank entry if none can be read
     */
    @SuppressWarnings("unchecked")
    public <T extends IEntry> T readEntryFromFile(Context context, String entryName, Class<T> tClass) {
        String fileContent = getFileContent(context, entryName + ".json");
        if (fileContent == null || fileContent.equals("")) {
            try {
                return (T) tClass.newInstance().getNull();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            return null;
        }
        // This might return something not great if called on the wrong entry type, but nothing i can do about that
        return gson.fromJson(getFileContent(context, entryName + ".json"), tClass);
    }

    /**
     * Reads all the entries in the folder and attempts to read all the entries in the folder as a specified entry type
     * , may return bad data if you try to read different kinds of entries to the ones that were stored so make sure to
     * not do that
     * @param context context associated with the app, use getContext()
     * @param tClass the class of the entries, must be of type T
     * @param <T> the type of the entries to read, must match the class
     * @return An ArrayList of the entries that were read, may be blank
     */
    public <T extends IEntry> ArrayList<T> readAllEntries(Context context, Class<T> tClass) {
        ArrayList<String> names = getListOfEntries(context);
        ArrayList<T> ret = new ArrayList<>();
        for (String s : names) {
            ret.add(readEntryFromFile(context, s, tClass));
        }
        return ret;
    }

    /**
     * Write the passed entry to a file (with the name specified by the entry) in the loaders storage location
     * @param context context associated with the app, use getContext()
     * @param entry the entry to write
     */
    public void writeEntryToFile(Context context, IEntry entry) {
        String content = gson.toJson(entry);
        setFileContent(context, entry.getName() + ".json", content);
    }

    /**
     * Write a collection of entries to files in the loaders storage location,
     * the same as iterating through a list and calling writeEntryToFile
     * @param context context associated with the app, use getContext()
     * @param entries the collection of entries to write
     */
    public void writeAllEntries(Context context, Collection<IEntry> entries) {
        for (IEntry e : entries) {
            writeEntryToFile(context, e);
        }
    }

    /**
     * Delete an entries file
     * @param context context associated with the app, use getContext()
     * @param entry the entry to be removed
     */
    public void removeEntry(Context context, IEntry entry) {
        removeFile(context, entry.getName() + ".json");
    }

    /**
     * Gets a list of files in the directory
     * @param context context associated with the app, use getContext()
     * @return A list of all file addresses in the directory, may be empty
     */
    private ArrayList<File> getListOfFiles(Context context) {
        File dir = getDirectory(context);
        File [] allFiles = dir.listFiles();
        ArrayList<File> ret = new ArrayList<>();
        if (allFiles != null) {
            ret.addAll(Arrays.asList(allFiles));
        }
        return ret;
    }

    /**
     * Gets a list of all the names of the entries in the directory
     * @param context context associated with the app, use getContext()
     * @return A list of the the entry names of the entries in the directory
     */
    public ArrayList<String> getListOfEntries(Context context) {
        File dir = getDirectory(context);
        File [] allFiles = dir.listFiles();
        ArrayList<String> ret = new ArrayList<>();
        if (allFiles != null) {
            for (File f : allFiles) {
                ret.add(f.getName().substring(0,f.getName().length() - 5)); // Remove the .json
            }
        }
        return ret;
    }

    /**
     * Gets the file object representing the android context given directory for this app
     * If told to look for external and the no external drive is connected will default to internal <br /><br />
     * Don't store this long term as it may change over time
     * @param context context associated with the app, use getContext()
     * @return A file representing the directory with the JSONLoader specified name using the
     * specified storage method
     */
    private File getDirectory(Context context) {
        // For all of these, find the correct absolute path using system methods, then add on the relative directory
        if (storageType == StorageType.EXTERNAL_PRIVATE) {
            if (isExternalStorageWritable()) {
                return new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileDirectory);
            }
        }
        if (storageType == StorageType.EXTERNAL_PUBLIC) {
            if (isExternalStorageWritable()) {
                return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileDirectory);
            }
        }
        if (noDirectory) {
            return context.getFilesDir();
        } else {
            return context.getDir(fileDirectory , Context.MODE_PRIVATE);
        }
    }

    /**
     * Sets the named files content/creates the named file and fills it with the set content
     * @param context context associated with the app, use getContext()
     * @param fileName the name of the file (within the Loader specified directory)
     * @param content the content to write to the file
     */
    private void setFileContent(Context context, String fileName, String content) {
        File f = getDirectory(context);
        try {
            f = new File(f, fileName);
            FileOutputStream outputStream = new FileOutputStream(f);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the named file and returns the content, returns "" if there is no content
     * @param context context associated with the app, use getContext()
     * @param fileName the name of the file (within the Loader specified directory)
     * @return the content of the specified file within the directory
     */
    private String getFileContent(Context context, String fileName) {
        String ret = "";
        File f = getDirectory(context);
        f = new File(f, fileName);
        if (!f.exists()) // Check it exists
            return ret;
        try { // File reading magic, efficient (maybe), robust (hopefully), functional (so far)
            InputStream inputStream = new FileInputStream(f);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            ret = stringBuilder.toString();
        } catch (Exception e) {
            Log.w("WPCA", "File failed to load: " + f.getPath());
        }
        return ret;
    }

    private void removeFile(Context context, String fileName) {
        File f = getDirectory(context);
        f = new File(f, fileName);

        if (!f.delete()) {
            Log.w("WPCA", "File failed to delete: " + f.getPath());
        }
    }

    /**
     * Check if external storage can be read from and written to
     * @return if external storage can be written to
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Where data should be stored
     */
    public enum StorageType {
        /**
         * Internal private storage, inaccessible by users
         */
        INTERNAL,
        /**
         * External private storage, inaccessible by users
         */
        EXTERNAL_PRIVATE,
        /**
         * External storage accessible by users
         */
        EXTERNAL_PUBLIC
    }
}
