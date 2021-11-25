package com.cmat.wpca.data;

import android.content.Context;

import com.cmat.wpca.data.entry.IEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * An abstracted data storage class used for reading and writing collections of IEntry's to device storage in a efficient and consistent way with minimal file access'
 * This compartmentalises the code for JSON reading and file management away from the user. <br /><br />
 * Uses a HashMap for internal storage so can only include one entry with a given name
 * @param <T>
 */
@SuppressWarnings("unused")
public class DataStore <T extends IEntry> {
    HashMap<String, T> data = new HashMap<>();
    HashMap<String, T> removedData = new HashMap<>();

    ArrayList<String> diffList = new ArrayList<>(); // List of entries that have changed since last refresh
    ArrayList<String> removeList = new ArrayList<>(); // List of entries that have been removed since last refresh

    JSONLoader loader;
    Class<T> entryClass;

    String selected; // the currently selected entry in the store

    ArrayList<String> selectedArray = new ArrayList<>(); // Selected entries

    boolean loaded;
    boolean modified;

    /**
     * An abstracted data storage class used for reading and writing collections of IEntry's to device storage in a efficient and consistent way with minimal file access'
     * @param directoryName the name of the directory in which to store the data, this should be unique to this DataStore
     * @param entryClass the type of entry to be stored in this data store
     */
    public DataStore(String directoryName, Class<T> entryClass) {
        loaded = false;
        modified = false;
        this.entryClass = entryClass;
        selected = "";
        loader = new JSONLoader(directoryName, JSONLoader.StorageType.INTERNAL);
    }

    /**
     * Loads all the data in the specified folder into the DataStore's memory
     * @param context context associated with the app, use getContext()
     */
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

    /**
     * Writes ALL data in the DataStore's memory to the disk, only call if saving a completely new set of data
     * @param context context associated with the app, use getContext()
     */
    public void write(Context context) {
        for (String e : data.keySet()) {
            loader.writeEntryToFile(context, data.get(e));
        }
    }

    /**
     * Refreshes the DataStore with the disk, if any entries have been modified or removed, the sync them with the ones on the disk
     * @param context context associated with the app, use getContext()
     */
    public void refresh(Context context) {
        if (!modified) {
            return;
        }
        for (String s : diffList) {
            loader.writeEntryToFile(context, data.get(s));
        }
        for (String s : removeList) {
            loader.removeEntry(context, Objects.requireNonNull(removedData.get(s), "no data found in remove list"));
            removedData.remove(Objects.requireNonNull(removedData.get(s), "no data found in remove list").getName());
        }
        diffList.clear();
        removeList.clear();
        modified = false;
    }

    /**
     * Mark an entry as having been modified, marked for syncing with the disk
     * @param name the name of the entry
     */
    public void markModified(String name) {
        modified = true;
        diffList.remove(name);
        diffList.add(name);
    }

    /**
     * Get the entry with the specified name, returning a blank entry if no entry is found with that name
     * @param name the name of the entry
     * @return the entry with the specified name, or a blank entry from T if no entry is found
     */
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

    /**
     * Get a list of entries with the specific name, returning a blank entry if no entry is found with that name
     * @param names the names of the entries
     * @return a list of entries with the specified names, or blank entries from T if no entry was found with a name
     */
    public ArrayList<T> getEntries(ArrayList<String> names) {
        ArrayList<T> ret = new ArrayList<>();
        for (String name : names) {
            ret.add(getEntry(name));
        }
        return ret;
    }

    /**
     * Get a list of entries with the specific name, returning a blank entry if no entry is found with that name
     * @param names the names of the entries
     * @return a list of entries with the specified names, or blank entries from T if no entry was found with a name
     */
    public ArrayList<T> getEntries(String[] names) {
        ArrayList<T> ret = new ArrayList<>();
        for (String name : names) {
            ret.add(getEntry(name));
        }
        return ret;
    }

    /**
     * Puts the entry into the data, if it or one with its name already exists overwriting it
     * @param entry the entry to add
     */
    public void setEntry(T entry) {
        data.remove(entry.getName());
        data.put(entry.getName(), entry);
        modified = true;
        diffList.add(entry.getName());
    }

    /**
     * Removes the given entry from the data, also marking it to be removed from the disk
     * @param entry the entry to remove
     */
    public void removeEntry(T entry) {
        modified = true;
        removeList.add(entry.getName());
        removedData.put(entry.getName(), entry);
        data.remove(entry.getName());
    }

    /**
     * Get an array of the names of all the Entry's in the DataStore
     * @return array of names of all Entry's in the DataStore
     */
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

    /**
     * Gets how many entry's are in this store
     * @return how many entry's are in this store
     */
    public int getCount() {
        return data.size();
    }

    /**
     * Sets an entry(s) to be selected
     * @param selected which entry(s) to select
     */
    public void setSelected(ArrayList<String> selected) {
        this.selectedArray = selected;
    }

    /**
     * Sets an entry(s) to be selected
     * @param selected which entry(s) to select
     */
    public void setSelected(String[] selected) {
        this.selectedArray = new ArrayList<>(Arrays.asList(selected));
    }

    /**
     * Sets an entry(s) to be selected
     * @param selected which entry(s) to select
     */
    public void setSelected(String selected) {
        this.selected = selected;
    }

    /**
     * Get the selected entry
     * @return the selected entry
     */
    public T getSelected() {
        return getEntry(selected);
    }

    /**
     * Gets all selected entries if multiple are selected
     * @return selected entries
     */
    public ArrayList<T> getSelectedArray() {
        ArrayList<T> ret = new ArrayList<>();
        for (String s : selectedArray) {
            ret.add(getEntry(s));
        }
        return ret;
    }
}
