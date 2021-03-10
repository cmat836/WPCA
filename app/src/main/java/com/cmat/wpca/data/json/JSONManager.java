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
import java.util.Scanner;

public class JSONManager<T extends IEntry> {
    public static JSONManager<BlankEntry> blankManager = new JSONManager<BlankEntry>();

    File directoryPath;
    String directoryName;

    StorageType storageType;

    private JSONManager() {
    }

    public HashMap<String, T> load(Context context) {
        HashMap<String, T> ret = new HashMap<String, T>();
        if (storageType == StorageType.EXTERNAL) {
            // Do External Load
        } else
        if (storageType == StorageType.INTERNAL) {
            /**
            File[] files = directoryPath.listFiles();
            ArrayList<String> filecontent = new ArrayList<>();
            for (File f : files)  {
                filecontent.add(getFileContent(context, f));
            }**/
            ret.put("No Rulesets Found", null);

        }
        return ret;
    }

    private String getFileContent(Context context, String directory, String filename) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (Exception e) {
        }

        return ret;
    }

    public void setFileContent(Context context, String fileName, String content) {
        try {
            FileOutputStream outputStream = context.openFileOutput(new File(directoryName, fileName).getName(), Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFile(Context context, String directory, String fileName) {
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

    public void createFile(Context context, String fileName) {
        createFile(context, directoryName, fileName);
    }

    public static class Builder<T extends IEntry> {
        String directoryName = "";
        StorageType storageType = StorageType.INTERNAL;

        public Builder(String directoryName) {
            this.directoryName = directoryName;
        }

        public Builder<T> setStorageType(StorageType type) {
            this.storageType = type;
            return this;
        }

        public JSONManager<T> build() {
            JSONManager<T> built = new JSONManager<T>();
            built.directoryName = directoryName;
            built.storageType = storageType;
            return built;
        }
    }

    public enum StorageType {
        INTERNAL,
        EXTERNAL
    }
}
