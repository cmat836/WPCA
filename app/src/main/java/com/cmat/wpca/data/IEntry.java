package com.cmat.wpca.data;

import com.google.gson.Gson;

public interface IEntry {
    String getName();
    IEntry getNull();
    IEntry getFromJSON(Gson gson, String json);
    String getJSON(Gson gson);
}
