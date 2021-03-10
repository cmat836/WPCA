package com.cmat.wpca.data;

public class BlankEntry implements IEntry {
    public static BlankEntry blankEntry = new BlankEntry();

    public IEntry getNull() {
        return blankEntry;
    }
}
