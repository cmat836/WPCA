package com.cmat.wpca.data;

public class StringUtil {
    /**
     * Returns the string passed with the first letter Uppercase and the rest of the letters lowercase
     * @param toFormat The string to capitalise
     * @return the capitalised string
     */
    public static String capitalised(String toFormat) {
        if (toFormat.length() < 1) {
            return toFormat;
        } else if (toFormat.length() > 1) {
            return toFormat.substring(0, 1).toUpperCase() + toFormat.substring(1).toLowerCase();
        }
        return toFormat.toUpperCase();
    }
}
