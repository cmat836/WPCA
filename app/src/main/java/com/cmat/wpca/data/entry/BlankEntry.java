package com.cmat.wpca.data.entry;

public class BlankEntry implements IEntry {
    public static BlankEntry blankEntry = new BlankEntry();

    @Override
    public String getName() {
        return "blank";
    }

    public IEntry getNull() {
        return blankEntry;
    }

}
