package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import org.apache.commons.io.IOUtils;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class TimeDependentActions {

    private static Integer counter = 0;

    public static void addCount() {
        counter++;
        new ResponseHandler().sendMessageToAdmin("Updated to: " + counter);
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        System.out.println("Context Refreshed!");
        try {
            restoreValues();
        } catch (NullPointerException e) {
            System.out.println("Exception on refresh: " + e.getMessage());
        }
    }

    @EventListener
    public void handleContextStart(ContextStartedEvent event) {
        System.out.println("Context Start!");
        try {
            restoreValues();
        } catch (NullPointerException e) {
            System.out.println("Exception on start: " + e.getMessage());
        }
    }

    public static void backupValues() {
        new FileProcessing().saveFile("BackupValues", String.valueOf(counter), "temp/");
    }

    public static void restoreValues() {
        try {
            InputStream stream = new FileProcessing().getFile("temp/BackupValues");
            if (stream != null) {
                String values = IOUtils.toString(stream, StandardCharsets.UTF_8);
                System.out.println(values);
                counter = Integer.valueOf(values);
            }
        } catch (IOException | NullPointerException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

//    private int attemptCounter = 0;
//
//    @PostConstruct
//    public void restore() {
//        if (attemptCounter > 10) return;
//        try {
//            restoreValues();
//            System.out.println("Success");
//        } catch (NullPointerException e) {
//            attemptCounter++;
//            try {
//                System.out.println("Waiting...");
//                Thread.sleep(1500);
//                restore();
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }

}
