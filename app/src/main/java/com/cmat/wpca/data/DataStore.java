package com.cmat.wpca.data;

import android.content.Context;

import com.cmat.wpca.data.entry.IEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataStore <T extends IEntry> {
    HashMap<String, T> data = new HashMap<>();
    HashMap<String, T> removedData = new HashMap<>();

    ArrayList<String> diffList = new ArrayList<>();
    ArrayList<String> removeList = new ArrayList<>();

    JSONLoader loader;
    Class<T> entryClass;

    String selected;

    ArrayList<String> selectedArray = new ArrayList<>();

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
        for (String s : removeList) {
            loader.removeEntry(context, removedData.get(s));
            removedData.remove(removedData.get(s).getName());
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

    public void removeEntry(T entry) {
        modified = true;
        removeList.add(entry.getName());
        removedData.put(entry.getName(), entry);
        data.remove(entry.getName());
    }

    public String[] getArrayOfEntryNames() {
        String[] s;
        if (data.size() == 0) {
            //s = new String[] { "No Data found"};
            s = new String[] {};
            return s;
        } else {
            s = new String[data.keySet().size()];
            return data.keySet().toArray(s);
        }
    }

    public String[] getArrayOfEntryNames(boolean includeNone) {
        String[] s;
        if (!includeNone) {
            return getArrayOfEntryNames();
        }
        if (data.size() == 0) {
            s = new String[] { "none"};
        } else {
            String[] t = new String[data.keySet().size()];
            s = new String[data.keySet().size() + 1];
            s[0] = "none";
            System.arraycopy(data.keySet().toArray(t), 0, s, 1, data.keySet().size());
        }
        return s;
    }

    public int getCount() {
        return data.size();
    }

    public void setSelected(ArrayList<String> selected) {
        this.selectedArray = selected;
    }

    public void setSelected(String[] selected) {
        this.selectedArray = new ArrayList<String>(Arrays.asList(selected));
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public T getSelected() {
        return getEntry(selected);
    }

    public ArrayList<T> getSelectedArray() {
        ArrayList<T> ret = new ArrayList<>();
        for (String s : selectedArray) {
            ret.add(getEntry(s));
        }
        return ret;
    }
}
