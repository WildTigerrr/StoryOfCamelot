package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@DependsOn({"filesProcessing", "amazonClient"})
public class TimeDependentActions {

    private static Integer counter = 0;
    private static ArrayList<String> actions = new ArrayList<>();

    public static void addCount() {
        counter++;
//        actions.add(String.valueOf(counter));
        new ResponseHandler().sendMessageToAdmin("Updated to: " + counter);
    }

    private static FileProcessing fileService;

    @Autowired
    private TimeDependentActions(FileProcessing fileService) {
        TimeDependentActions.fileService = fileService;
    }

    @PostConstruct
    public void restoreValuesFromBackup() {
        System.out.println("TimeDependentActions > restoreValuesFromBackup: Attempt to restore values from backup");
        restoreValues();
        System.out.println("TimeDependentActions > restoreValuesFromBackup: Attempt finished");
    }

    public static void backupValues() {
        System.out.println("TimeDependentActions > backupValues: Creating backup");
        fileService.saveFile("temp/", "BackupValues", listToString()); //String.valueOf(counter)
        System.out.println("TimeDependentActions > backupValues: Backup created");
    }

    private static void restoreValues() {
        try {
            InputStream stream = fileService.getFile("temp/BackupValues"); // If file not found > AmazonS3Exception
            if (stream != null) {
                String values = IOUtils.toString(stream, StandardCharsets.UTF_8);
                System.out.println(values);
                stringToList(values);
//                counter = Integer.valueOf(values);
                counter = 10;
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public static void initList() {
        actions = new ArrayList<>();
        actions.add("Test");
    }

    public static void addElement(String str) {
        actions.add(str);
    }

    public static void removeFirst() {
        if (actions != null && !actions.isEmpty()) {
            actions.remove(0);
        }
    }

    public static void getAll() {
        new ResponseHandler().sendMessageToAdmin(listToString());
    }

    private static String listToString() {
        if (actions == null || actions.isEmpty()) {
            return "List is empty";
        }
        StringBuilder data = new StringBuilder();
        for (String action : actions) {
            data.append(";").append(action);
        }
        data.deleteCharAt(0);
        return data.toString();
    }

    private static void stringToList(String data) {
        actions = new ArrayList<>();
        if (data != null) {
            actions.addAll(Arrays.asList(data.split(";")));
        }
    }

    private static ScheduledExecutorService scheduledExecutorService;
    private static ScheduledFuture<?> task;

    private static void cancel() {
        task.cancel(false);
    }

    private static void check() {
        System.out.println("Do something");
    }

    private static void schedule() {
        if (scheduledExecutorService == null)
            scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        task = scheduledExecutorService.scheduleAtFixedRate(
                TimeDependentActions::check, 0, 5, TimeUnit.SECONDS);
    }

}
