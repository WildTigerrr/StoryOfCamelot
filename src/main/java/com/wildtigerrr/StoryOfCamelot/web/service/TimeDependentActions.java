package com.wildtigerrr.StoryOfCamelot.web.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@DependsOn({"filesProcessing","amazonClient"})
public class TimeDependentActions {

    private static FileProcessing fileService;
    @Autowired
    private TimeDependentActions(FileProcessing fileService) {
        System.out.println("Autowiring FileService");
        TimeDependentActions.fileService = fileService;
    }

    private static Integer counter = 0;

    public static void addCount() {
        counter++;
        new ResponseHandler().sendMessageToAdmin("Updated to: " + counter);
    }

    @PostConstruct
    public void init() {
        System.out.println("Post construct");
        restoreValues();
        System.out.println("Re-creation");
        backupValues();
    }

    private static ScheduledFuture<?> task;

    @PostConstruct
    public void initialization() {
        System.out.println("Context Start!");
        restoreValues();
//        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
//        task = scheduledExecutorService.scheduleAtFixedRate(
//                TimeDependentActions::restoreValues, 0, 2, TimeUnit.SECONDS);
    }

    public static void backupValues() {
        fileService.saveFile("BackupValues", String.valueOf(counter), "temp/");
    }

    public static void restoreValues() {
        if (fileService == null) {
            System.out.println("FileService null");
            return;
        }
        try {
            InputStream stream = fileService.getFile("temp/BackupValues");
            if (stream != null) {
//                task.cancel(true);
                String values = IOUtils.toString(stream, StandardCharsets.UTF_8);
                System.out.println(values);
                counter = Integer.valueOf(values);
            }
        } catch (IOException | AmazonS3Exception e) {
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
