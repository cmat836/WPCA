package com.cmat.wpca.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

public class DataStore <T extends IEntry> {
    HashMap<String, T> data = new HashMap<>();

    ArrayList<String> diffList = new ArrayList<>();

    JSONLoader loader;
    Class<T> entryClass;

    String selected;

    boolean loaded;
    boolean modified;

    public DataStore(String directoryName, Class<T> entryClass) {
        loaded = false;
        modified = false;
        this.entryClass = entryClass;
        selected = "";
        loader = new JSONLoader(directoryName, JSONLoader.StorageType.INTERNAL);
    }

    public void load(Context context) {
        if (!loaded) {
            for (T entry : loader.readAllEntries(context, entryClass)) {
                data.put(entry.getName(), entry);
            }
            loaded = true;
        } else {
            refresh(context);
        }
    }

    public void write(Context context) {
        for (String e : data.keySet()) {
            loader.writeEntryToFile(context, data.get(e));
        }
    }

    public void refresh(Context context) {
        if (!modified) {
            return;
        }
        for (String s : diffList) {
            loader.writeEntryToFile(context, data.get(s));
        }
    }

    public void markModified(String name) {
        modified = true;
        diffList.remove(name);
        diffList.add(name);
    }

    public T getEntry(String name) {
        T ret = data.get(name);
        if (ret != null) {
            return ret;
        }
        try {
            return entryClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setEntry(T entry) {
        data.remove(entry.getName());
        data.put(entry.getName(), entry);
        modified = true;
        diffList.add(entry.getName());
    }

    public String[] getArrayOfEntryNames() {
        String[] s;
        if (data.size() == 0) {
            s = new String[] { "No Data found"};
            return s;
        } else {
            s = new String[data.keySet().size()];
            return data.keySet().toArray(s);
        }
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public T getSelected() {
        return getEntry(selected);
    }
}
