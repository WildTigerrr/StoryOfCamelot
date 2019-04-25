package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@DependsOn("AmazonClient")
public class TimeDependentActions {

    private static Integer counter = 0;

    public static void addCount() {
        counter++;
        new ResponseHandler().sendMessageToAdmin("Updated to: " + counter);
    }

    public static void backupValues() {
        new FileProcessing().saveFile("BackupValues", String.valueOf(counter), "temp/");
    }

    @PostConstruct
    public static void restoreValues() {
        try {
            InputStream stream = new FileProcessing().getFile("temp/BackupValues");
            if (stream != null) {
                String values = IOUtils.toString(stream, StandardCharsets.UTF_8);
                System.out.println(values);
                counter = Integer.valueOf(values);
            }
        } catch (IOException e) {
            new ResponseHandler().sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
    }




}
