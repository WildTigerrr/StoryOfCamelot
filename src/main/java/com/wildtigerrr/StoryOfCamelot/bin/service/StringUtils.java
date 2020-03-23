package com.wildtigerrr.StoryOfCamelot.bin.service;

import java.lang.reflect.Type;
import java.math.BigInteger;

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

    public static String getClassName(Type type) {
        String classPath = type.getTypeName();
        return classPath.substring(classPath.lastIndexOf('.') + 1);
    }

    public static String getNumberWithMaxRadix(long value) {
        return new BigInteger(String.valueOf(value)).toString(36);
    }

    public static long getNumberWithMaxRadix(String value) {
        return Long.parseLong(new BigInteger(value, 36).toString(10));
    }

}