package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.ActionType;
import com.wildtigerrr.StoryOfCamelot.bin.handler.MoveCommandHandler;
import com.wildtigerrr.StoryOfCamelot.bin.service.Scheduler;
import com.wildtigerrr.StoryOfCamelot.bin.service.ScheduledAction;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import lombok.Getter;
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

@Service
@Log4j2
public class TimeDependentActions {

    private static FileProcessing fileService;
    private static MoveCommandHandler movement;
    private static ResponseManager messages;

    @Autowired
    private TimeDependentActions(FileProcessing fileService, MoveCommandHandler moveHandler, ResponseManager messages) {
        TimeDependentActions.fileService = fileService;
        TimeDependentActions.movement = moveHandler;
        TimeDependentActions.messages = messages;
    }

    @Getter
    private static HashMap<Long, ScheduledAction> scheduledActionMap = new HashMap<>();
    @Getter
    private static HashMap<String, List<Long>> playerToScheduled = new HashMap<>();

    public static void scheduleAction(ScheduledAction action) {
        while (scheduledActionMap.containsKey(action.timeToExecute)) action.timeToExecute++;
        List<Long> playerActions;
        if (playerToScheduled.containsKey(action.playerId)) {
            playerActions = playerToScheduled.get(action.playerId);
        } else {
            playerActions = new ArrayList<>();
        }
        playerActions.add(action.timeToExecute);
        playerToScheduled.put(action.playerId, playerActions);
        scheduledActionMap.put(action.timeToExecute, action);
        startActionsCheck();
    }

    private static void check() {
        try {
            proceedCheck();
        } catch (Exception e) {
            messages.sendErrorReport(e);
        }
    }

    private static void proceedCheck() {
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
        playerActions.remove(actionEntry.getValue().timeToExecute);
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


    // =========================================== Backup-Restore Utils =========================================== //


    @PostConstruct
    public void restoreValuesFromBackup() {
        log.debug("Attempt to restore values from backup");
        restoreValues();
        log.info("Actions Restored Successfully");
    }

    public static void backupValues() {
        log.debug("Creating Actions Backup");
        String data = "scheduledActionMap===" + scheduledActionMapToString(); // May add more separated by "|||"
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
                    if ("scheduledActionMap".equals(line[0])) {
//                        stringToScheduledActionMap(line[1]); // Uncomment when DB would be stable
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error During Restoring Actions", e);
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
            scheduledActionMap.put(action.timeToExecute, action);
            if (playerToScheduled.containsKey(action.playerId)) {
                schedules = playerToScheduled.get(action.playerId);
                schedules.add(action.timeToExecute);
                playerToScheduled.put(action.playerId, schedules);
            }
        }
        startActionsCheck();
    }

}