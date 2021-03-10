package com.cmat.wpca.data;

import android.content.Context;

import com.cmat.wpca.data.json.JSONManager;

import java.lang.reflect.Array;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;

public class DataStore <T extends IEntry> {
    HashMap<String, T> data = new HashMap<>();

    JSONManager<T> manager;

    boolean loaded;

    public DataStore() {
        loaded = false;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void load(Context context, JSONManager.Builder<T> managerBuilder) {
        manager = managerBuilder.build();
        data = manager.load(context);
        loaded = true;
    }

    public void makeBumFile(Context context) {
        manager.createFile(context, "bums", "bumfile.txt");
        manager.setFileContent(context, "bumfile.txt", "I like bums");
    }

    public String[] getDataAsArray() {
        if (!loaded) {
            return new String[] { "" };
        }
        String[] s = new String[data.keySet().size()];
        return data.keySet().toArray(s);
    }
}