package com.cmat.wpca.data;

import com.google.gson.Gson;

public class BlankEntry implements IEntry {
    public static BlankEntry blankEntry = new BlankEntry();

    @Override
    public String getName() {
        return "blank";
    }

    public IEntry getNull() {
        return blankEntry;
    }

    @Override
    public IEntry getFromJSON(Gson gson, String json) {
        return new BlankEntry();
    }

    @Override
    public String getJSON(Gson gson) {
        return gson.toJson(this);
    }
}
