package com.wildtigerrr.StoryOfCamelot.bin.service;

import java.util.List;

public class ListUtils {

    public static boolean isEmpty(List<?> object) {
        return object == null || object.isEmpty();
    }

    public static boolean isNotEmpty(List<?> object) {
        return !isEmpty(object);
    }

}
