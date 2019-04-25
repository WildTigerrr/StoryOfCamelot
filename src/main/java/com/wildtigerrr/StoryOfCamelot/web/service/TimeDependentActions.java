package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class TimeDependentActions {

    private static Integer counter = 0;

    public void addCount() {
        counter++;
        new ResponseHandler().sendMessageToAdmin("Updated to: " + counter);
    }

    public static void backupValues() {
        new FileProcessing().saveFile("BackupValues", String.valueOf(counter), "temp/");
    }

    public static void restoreValues() {
        try {
            String values = IOUtils.toString(new FileProcessing().getFile("temp/BackupValues"), StandardCharsets.UTF_8);
            System.out.println(values);
            counter = Integer.valueOf(values);
        } catch (IOException e) {
            new ResponseHandler().sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
    }




}
