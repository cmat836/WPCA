package com.cmat.wpca.data.json;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.cmat.wpca.data.BlankEntry;
import com.cmat.wpca.data.Game;
import com.cmat.wpca.data.GameRecord;
import com.cmat.wpca.data.IEntry;
import com.cmat.wpca.data.PlayerEntry;
import com.cmat.wpca.data.RulesetEntry;
import com.cmat.wpca.data.TeamEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JSONManager<T extends IEntry> {
    public static JSONManager<BlankEntry> blankManager = new JSONManager<BlankEntry>();

    String directoryName;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    StorageType storageType;
    T entryType;

    private JSONManager() {
    }

    public HashMap<String, T> load(Context context) {
        HashMap<String, T> ret = new HashMap<String, T>();
        if (storageType == StorageType.EXTERNAL) {
            // Do External Load
        } else
        if (storageType == StorageType.INTERNAL) {
            File dir = getDirectory(context);
            File [] allFiles = dir.listFiles();
            ArrayList<String> fileContent = new ArrayList<>();

            if (allFiles != null) {
                for (File f : allFiles) {
                    fileContent.add(getFileContent(context, f));
                }
            }
            if (fileContent.size() == 0) {
            }
            for (String s : fileContent) {
                IEntry entry = entryType.getFromJSON(gson, s);
                ret.put(entry.getName(), (T) entry);
            }
        }
        return ret;
    }

    public void write(Context context, HashMap<String, T> data) {
        if (storageType == StorageType.EXTERNAL) {
            // Do External Write
        } else
        if (storageType == StorageType.INTERNAL) {
            for (String e : data.keySet()) {
                String content = data.get(e).getJSON(gson);
                setFileContent(context, directoryName,  e + ".json", content);
            }
        }
    }

    private File getDirectory(Context context) {
        File f;
        if (directoryName == "")
            f = context.getFilesDir();
        else
            f = context.getDir(directoryName ,context.MODE_PRIVATE);
        return f;
    }

    private String getFileContent(Context context, String fileName) {
        return getFileContent(context, directoryName, fileName);
    }

    private String getFileContent(Context context, String directory, String fileName) {
        String ret = "";
        File f;
        if (directory == "")
            f = context.getFilesDir();
        else
            f = context.getDir(directory ,context.MODE_PRIVATE);
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
        }

        return ret;
    }

    private String getFileContent(Context context, File fileName) {
        return getFileContent(context, fileName.getName());
    }

    private void setFileContent(Context context, String directory, String fileName, String content) {
        File f;
        if (directory == "")
            f = context.getFilesDir();
        else
            f = context.getDir(directory ,context.MODE_PRIVATE);
        try {
            f = new File(f, fileName);
            FileOutputStream outputStream = new FileOutputStream(f); //context.openFileOutput(f., Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFileContent(Context context, String fileName, String content) {
        setFileContent(context, directoryName, fileName, content);
    }

    private void setFileContent(Context context, File fileName, String content) {
        setFileContent(context, fileName.getName(), content);
    }

    private void createFile(Context context, String directory, String fileName) {
        File f;
        if (directory == "")
            f = context.getFilesDir();
        else
            f = context.getDir(directory ,context.MODE_PRIVATE);

        f = new File(f, fileName);
        if (f.exists())
            return;

        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile(Context context, String fileName) {
        createFile(context, directoryName, fileName);
    }

    public static class Builder<T extends IEntry> {
        String directoryName = "";
        StorageType storageType = StorageType.INTERNAL;
        T strat;

        public Builder(String directoryName, T type) {
            this.directoryName = directoryName;
            strat = type;
        }

        public Builder<T> setStorageType(StorageType type) {
            this.storageType = type;
            return this;
        }

        public JSONManager<T> build() {
            JSONManager<T> built = new JSONManager<T>();
            built.directoryName = directoryName;
            built.storageType = storageType;
            built.entryType = strat;
            return built;
        }
    }

    public enum StorageType {
        INTERNAL,
        EXTERNAL
    }
}
