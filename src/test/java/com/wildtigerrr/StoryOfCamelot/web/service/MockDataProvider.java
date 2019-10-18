package com.wildtigerrr.StoryOfCamelot.web.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Service
@Profile("test")
public class MockDataProvider implements DataProvider {


    @Override
    public InputStream getObject(String filePath) {
        return null;
    }

    @Override
    public void saveFile(String name, File file) {

    }

    @Override
    public void saveString(String name, String data) {

    }
}
