package com.wildtigerrr.StoryOfCamelot.bin.service;

public class StringUtils {

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public static String escape(String str) {
        return str == null ? null : str
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("`", "\\`");
    }

    public static String emptyIfOutOfBounds(String[] array, int index) {
        if (index > array.length) return "";
        return array[index];
    }

}