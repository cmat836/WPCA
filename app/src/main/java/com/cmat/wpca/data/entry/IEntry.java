package com.cmat.wpca.data.entry;

/**
 * A generic entry that can be stored to the disk
 */
public interface IEntry {
    /**
     * Gets the name of this entry
     * @return the name of this entry
     */
    String getName();

    /**
     * Gets a blank entry
     * @return a blank entry
     */
    IEntry getNull();
}
