package com.cmat.wpca.data.entry;

/**
 * Blank implementation of IEntry
 */
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
