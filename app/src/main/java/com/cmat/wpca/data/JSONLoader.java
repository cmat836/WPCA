package com.cmat.wpca.data;

import android.content.Context;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Environment;
import android.util.Log;

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
        this.fileDirectory = this.fileDirectory;
        this.storageType = storageType;
        noDirectory = false;
        if (fileDirectory == null || fileDirectory == "") {
            noDirectory = true;
        }
    }

    public <T extends IEntry> T getEntryFromFile(Context context, String fileName, Class<T> tClass) {
        return gson.fromJson(getFileContent(context, fileName), tClass);
    }

    public <T extends IEntry> ArrayList<T> getAllEntries(Context context, Class<T> tClass) {
        ArrayList<String> names = getListOfFileNames(context);
        ArrayList<T> ret = new ArrayList<>();
        for (String s : names) {
            ret.add(getEntryFromFile(context, s, tClass));
        }
        return ret;
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

    public ArrayList<String> getListOfFileNames(Context context) {
        File dir = getDirectory(context);
        File [] allFiles = dir.listFiles();
        ArrayList<String> ret = new ArrayList<>();
        if (allFiles != null) {
            for (File f : allFiles) {
                ret.add(f.getName());
            }
        }
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
