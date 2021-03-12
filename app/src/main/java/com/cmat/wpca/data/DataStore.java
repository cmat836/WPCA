package com.cmat.wpca.data;

import android.content.Context;
import android.util.Log;

import com.cmat.wpca.data.json.JSONManager;

import java.lang.reflect.Array;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;

public class DataStore <T extends IEntry> {
    HashMap<String, T> data = new HashMap<>();

    JSONManager<T> manager;

    boolean loaded;
    T entryType;

    public DataStore(T blank) {
        loaded = false;
        entryType = blank;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void load(Context context, JSONManager.Builder<T> managerBuilder) {
        manager = managerBuilder.build();
        data = manager.load(context);
        loaded = true;
    }

    public void write(Context context) {
        manager.write(context, data);
    }

    public void addEntry(T entry) {
        data.put(entry.getName(), entry);
    }

    public T getEntryByName(String name) {
        if (data.size() == 0) {
            return (T) entryType.getNull();
        } else {
            return data.get(name);
        }
    }



    public void refresh(Context context) {
        data = manager.load(context);
    }

    public String[] getDataAsArray() {
        if (!loaded) {
            return new String[] { "" };
        }
        String[] s;
        if (data.size() == 0) {
            s = new String[] { "No Data found"};
            return s;
        } else {
            s = new String[data.keySet().size()];
            return data.keySet().toArray(s);
        }
    }
}
