package com.wildtigerrr.StoryOfCamelot.bin.service;

public class StringUtils {

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

}