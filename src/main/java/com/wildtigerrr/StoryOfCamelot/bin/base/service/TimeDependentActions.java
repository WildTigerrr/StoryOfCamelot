package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.base.GameMovement;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ActionType;
import com.wildtigerrr.StoryOfCamelot.bin.service.Scheduler;
import com.wildtigerrr.StoryOfCamelot.bin.service.ScheduledAction;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Log4j2
@Service
public class TimeDependentActions {

    private static ArrayList<String> actions = new ArrayList<>();
    private static HashMap<Long, ScheduledAction> scheduledActionMap = new HashMap<>();
    private static HashMap<String, List<Long>> playerToScheduled = new HashMap<>();

    public static Boolean scheduleMove(String playerId, Long timestamp, String target, String distance) {
        while (scheduledActionMap.containsKey(timestamp)) timestamp++;
        List<Long> playerActions;
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
        startActionsCheck();
        return true;
    }

    private static FileProcessing fileService;
    private static GameMovement movement;

    @Autowired
    private TimeDependentActions(FileProcessing fileService, GameMovement gameMovement) {
        TimeDependentActions.fileService = fileService;
        TimeDependentActions.movement = gameMovement;
    }

    @PostConstruct
    public void restoreValuesFromBackup() {
        log.debug("Attempt to restore values from backup");
        restoreValues();
        log.info("Actions Restored Successfully");
    }

    public static void backupValues() {
        log.debug("Creating Actions Backup");
        String data = "actions===" + actionsToString() + "|||"
                + "scheduledActionMap===" + scheduledActionMapToString();
        fileService.saveFile("temp/", "BackupValues", data);
        log.info("Actions Backup Created");
    }

    private static void restoreValues() {
        try {
            log.trace("Attempt To Restore Values");
            InputStream stream = fileService.getFile("temp/BackupValues"); // If file not found > AmazonS3Exception
            if (stream != null) {
                String values = IOUtils.toString(stream, StandardCharsets.UTF_8);
                log.debug("Values To Restore: " + values);
                String[] line;
                for (String str : values.split("\\|\\|\\|")) {
                    line = str.split("===", 2);
                    if (line.length < 2) continue;
                    switch (line[0]) {
                        case "actions":
                            stringToActions(line[1]);
                            break;
                        case "scheduledActionMap":
//                            stringToScheduledActionMap(line[1]);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error During Restoring Actions", e);
        }
    }

    public static void addElement(String str) {
        actions.add(str);
    }

    public static void removeFirst() {
        if (actions != null && !actions.isEmpty()) {
            actions.remove(0);
        }
    }

    public static String getAll() {
        return scheduledActionMapToString();
    }

    private static String scheduledActionMapToString() {
        log.debug("Actions Map: " + scheduledActionMap);
        if (scheduledActionMap == null || scheduledActionMap.isEmpty()) {
            return "null";
        }
        StringBuilder data = new StringBuilder();
        for (Long key : scheduledActionMap.keySet()) {
            data.append(";").append(scheduledActionMap.get(key).toString());
        }
        log.debug("Scheduled: " + data);
        data.deleteCharAt(0);
        return data.toString();
    }

    private static void stringToScheduledActionMap(String data) {
        scheduledActionMap = new HashMap<>();
        playerToScheduled = new HashMap<>();
        if (data == null) return;
        ArrayList<String> values = new ArrayList<>(Arrays.asList(data.split(";")));
        ScheduledAction action;
        List<Long> schedules;
        for (String value : values) {
            action = new ScheduledAction(value);
            scheduledActionMap.put(action.timestamp, action);
            if (playerToScheduled.containsKey(action.playerId)) {
                schedules = playerToScheduled.get(action.playerId);
                schedules.add(action.timestamp);
                playerToScheduled.put(action.playerId, schedules);
            }
        }
        startActionsCheck();
    }

    private static String actionsToString() {
        log.debug("Actions: " + actions);
        if (actions == null || actions.isEmpty()) {
            return "null";
        }
        StringBuilder data = new StringBuilder();
        for (String action : actions) {
            data.append(";").append(action);
        }
        log.debug("Scheduled: " + data);
        data.deleteCharAt(0);
        return data.toString();
    }

    private static void stringToActions(String data) {
        actions = new ArrayList<>();
        if (data == null) return;
        actions.addAll(Arrays.asList(data.split(";")));
    }

    private static void check() {
        log.debug("Checking Active Actions");
        if (scheduledActionMap.isEmpty()) {
            cancelActionsCheck();
        } else {
            Iterator<Map.Entry<Long, ScheduledAction>> iterator = scheduledActionMap.entrySet().iterator();
            Long currentTime = Calendar.getInstance().getTimeInMillis();
            log.debug("Processing on Action: " + currentTime);
            while (iterator.hasNext()) {
                if (processCurrentAction(iterator.next(), currentTime)) iterator.remove();
            }
            if (scheduledActionMap.isEmpty()) cancelActionsCheck();
        }
    }

    private static Boolean processCurrentAction(
            Map.Entry<Long, ScheduledAction> actionEntry,
            Long currentTime
    ) {
        log.debug("Action: " + actionEntry.getValue());
        if (currentTime < actionEntry.getKey()) return false;
        log.debug("Updating location...");
        movement.sendLocationUpdate(scheduledActionMap.get(actionEntry.getKey()));
        log.debug("Location updated");
        String playerId = actionEntry.getValue().playerId;
        List<Long> playerActions = playerToScheduled.get(playerId);
        playerActions.remove(actionEntry.getValue().timestamp);
        if (playerActions.isEmpty()) {
            playerToScheduled.remove(playerId);
        } else {
            playerToScheduled.put(playerId, playerActions);
        }
        log.debug("Action Finished");
        return true;
    }

    private static ScheduledFuture<?> task;

    private static void startActionsCheck() {
        log.trace("Attempt To Start Checking");
        if (!Scheduler.isActive(task)) task = Scheduler.schedule(TimeDependentActions::check);
        log.debug("Start checking");
    }

    private static void cancelActionsCheck() {
        log.trace("Attempt To Stop Checking");
        Scheduler.cancel(task);
        log.debug("Stop checking");
    }

}