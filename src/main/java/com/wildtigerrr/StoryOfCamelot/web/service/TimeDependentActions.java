package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ActionType;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.database.DatabaseInteraction;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@DependsOn({"filesProcessing", "amazonClient"})
public class TimeDependentActions {

    private static Integer counter = 0;
    private static ArrayList<String> actions = new ArrayList<>();
    private static HashMap<Long, ScheduledAction> scheduledActionMap = new HashMap<>();
    private static HashMap<Integer, ArrayList<Long>> playerToScheduled = new HashMap<>();

    public static void addCount() {
        counter++;
//        actions.add(String.valueOf(counter));
        responseHandler.sendMessageToAdmin("Updated to: " + counter);
    }

    public static Boolean scheduleMove(int playerId, Long timestamp, String target, String distance) {
        while (scheduledActionMap.containsKey(timestamp)) timestamp++;
        ArrayList<Long> playerActions;
        if (playerToScheduled.containsKey(playerId)) {
            playerActions = playerToScheduled.get(playerId);
            for (Long key : playerActions) {
                if (scheduledActionMap.get(key).type == ActionType.MOVEMENT) {
                    return false;
                }
            }
            playerActions.add(timestamp);
            playerToScheduled.put(playerId, playerActions);
        } else {
            playerActions = new ArrayList<>();
            playerActions.add(timestamp);
            playerToScheduled.put(playerId, playerActions);
        }
        scheduledActionMap.put(timestamp, new ScheduledAction(
                timestamp,
                ActionType.MOVEMENT,
                playerId,
                target,
                distance
        ));
        startCheck();
        return true;
    }

    private static FileProcessing fileService;
//    private static DatabaseInteraction databaseInteraction;
    private static ResponseHandler responseHandler;

    @Autowired
    private TimeDependentActions(FileProcessing fileService, ResponseHandler responseHandler) { // DatabaseInteraction databaseInteraction,
        TimeDependentActions.fileService = fileService;
//        TimeDependentActions.databaseInteraction = databaseInteraction;
        TimeDependentActions.responseHandler = responseHandler;
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
        responseHandler.sendMessageToAdmin(listToString());
    }

    private static String listToString() {
        System.out.println(actions);
        if (actions == null || actions.isEmpty()) {
            System.out.println("Empty");
            return "List is empty";
        }
        StringBuilder data = new StringBuilder();
        for (String action : actions) {
            data.append(";").append(action);
        }
//        if (playerToScheduled != null && !playerToScheduled.isEmpty()) {
//            data.append("|||");
//            for () {
//
//            }
//        }
        System.out.println(data);
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

    private static void startCheck() {
        if (task == null || task.isCancelled()) schedule();
    }

    private static void cancel() {
        task.cancel(true);
    }

    private static void check() {
        System.out.println("Checking...");
        if (scheduledActionMap.isEmpty()) {
            cancel();
        } else {
            Iterator<Map.Entry<Long, ScheduledAction>> iterator = scheduledActionMap.entrySet().iterator();
            ArrayList<Long> playerActions;
            Integer playerId;
            Long currentTime = Calendar.getInstance().getTimeInMillis();
            while (iterator.hasNext()) {
                Map.Entry<Long, ScheduledAction> entry = iterator.next();
                if (currentTime > entry.getKey()) {
                    responseHandler.sendLocationUpdate(scheduledActionMap.get(entry.getKey()));
                    playerId = entry.getValue().playerId;
                    playerActions = playerToScheduled.get(playerId);
                    playerActions.remove(entry.getValue().timestamp);
                    if (playerActions.isEmpty()) {
                        playerToScheduled.remove(playerId);
                    } else {
                        playerToScheduled.put(playerId, playerActions);
                    }
                    System.out.println(playerActions);
                    System.out.println(playerToScheduled);
                    iterator.remove();
                    System.out.println("Item removed");
                }
            }
            if (scheduledActionMap.isEmpty()) cancel();
        }
    }

    private static void schedule() {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }
        task = scheduledExecutorService.scheduleAtFixedRate(
                TimeDependentActions::check, 5, 5, TimeUnit.SECONDS);
    }

}