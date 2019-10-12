package com.wildtigerrr.StoryOfCamelot.web.service;

import java.io.File;
import java.io.InputStream;

public interface DataProvider {

    InputStream getObject(String filePath);
    void saveFile(String name, File file);
    void saveString(String name, String data);

}
