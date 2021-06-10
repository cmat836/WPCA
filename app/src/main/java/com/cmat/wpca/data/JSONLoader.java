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
import java.util.Collection;

/**
 * A class to assist loading JSON files from the OS correct storage locations
 */
public class JSONLoader {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String fileDirectory; // The directory within storage that this Loader loads from/saves to
    private StorageType storageType;

    private boolean noDirectory;

    /**
     * A class to assist loading JSON files from the OS correct storage locations
     * @param fileDirectory The directory within storage that this Loader loads from/saves to, set to null or "" to use the default directory
     * @param storageType Internal or external storage
     */
    public JSONLoader(String fileDirectory, StorageType storageType) {
        this.fileDirectory = fileDirectory;
        this.storageType = storageType;
        noDirectory = false;
        if (fileDirectory == null || fileDirectory.equals("")) {
            noDirectory = true;
        }
    }

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
        return gson.fromJson(getFileContent(context, entryName + ".json"), tClass);
    }

    public <T extends IEntry> ArrayList<T> readAllEntries(Context context, Class<T> tClass) {
        ArrayList<String> names = getListOfEntries(context);
        ArrayList<T> ret = new ArrayList<>();
        for (String s : names) {
            ret.add(readEntryFromFile(context, s, tClass));
        }
        return ret;
    }

    public void writeEntryToFile(Context context, IEntry entry) {
        String content = gson.toJson(entry);
        setFileContent(context, entry.getName() + ".json", content);
    }

    public void writeAllEntries(Context context, Collection<IEntry> entries) {
        for (IEntry e : entries) {
            writeEntryToFile(context, e);
        }
    }

    public void removeEntry(Context context, IEntry entry) {
        removeFile(context, entry.getName() + ".json");
    }

    private ArrayList<File> getListOfFiles(Context context) {
        File dir = getDirectory(context);
        File [] allFiles = dir.listFiles();
        ArrayList<File> ret = new ArrayList<>();
        if (allFiles != null) {
            for (File f : allFiles) {
                ret.add(f);
            }
        }
        return ret;
    }

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
     * If told to look for external and the no external drive is connected will default to internal
     * @param context
     * @return
     */
    private File getDirectory(Context context) {

        if (storageType == StorageType.EXTERNAL_PRIVATE) {
            if (isExternalStorageWritable()) {
                return context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            }
        }
        if (storageType == StorageType.EXTERNAL_PUBLIC) {
            if (isExternalStorageWritable()) {
                return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            }
        }
        if (noDirectory) {
            return context.getFilesDir();
        } else {
            return context.getDir(fileDirectory ,context.MODE_PRIVATE);
        }
    }

    /**
     * Sets the named file/creates the named file and fills it with the set content
     * @param context
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
     * @param context
     * @param fileName the name of the file (within the Loader specified directory)
     * @return
     */
    private String getFileContent(Context context, String fileName) {
        String ret = "";
        File f = getDirectory(context);
        f = new File(f, fileName);
        if (!f.exists())
            return ret;
        try {
            InputStream inputStream = new FileInputStream(f);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            ret = stringBuilder.toString();
        } catch (Exception e) {
            Log.println(Log.WARN, "WPCA", "File failed to load: " + f.getPath());
        }
        return ret;
    }

    private void removeFile(Context context, String fileName) {
        File f = getDirectory(context);
        f = new File(f, fileName);
        boolean warning = f.delete();
    }

    /**
     * Check if external storage can be read from and written to
     * @return
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public enum StorageType {
        INTERNAL,
        EXTERNAL_PRIVATE,
        EXTERNAL_PUBLIC
    }
}
