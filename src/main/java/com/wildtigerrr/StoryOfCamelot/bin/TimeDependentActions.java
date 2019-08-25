package com.wildtigerrr.StoryOfCamelot.bin;

import com.wildtigerrr.StoryOfCamelot.bin.base.GameMovement;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ActionType;
import com.wildtigerrr.StoryOfCamelot.bin.exceptions.SOCInvalidDataException;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.bin.service.ScheduledAction;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(TimeDependentActions.class);

    private static ArrayList<String> actions = new ArrayList<>();
    private static HashMap<Long, ScheduledAction> scheduledActionMap = new HashMap<>();
    private static HashMap<Integer, ArrayList<Long>> playerToScheduled = new HashMap<>();

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
    private static GameMovement movement;
    private static ResponseManager messages;

    @Autowired
    private TimeDependentActions(FileProcessing fileService, GameMovement gameMovement, ResponseManager responseManager) {
        TimeDependentActions.fileService = fileService;
        TimeDependentActions.movement = gameMovement;
        TimeDependentActions.messages = responseManager;
    }

    @PostConstruct
    public void restoreValuesFromBackup() {
        logger.debug("Attempt to restore values from backup");
        restoreValues();
        logger.info("Actions Restored Successfully");
    }

    public static void backupValues() {
        logger.debug("Creating Actions Backup");
        String data = "actions===" + actionsToString() + "|||"
                + "scheduledActionMap===" + scheduledActionMapToString();
        fileService.saveFile("temp/", "BackupValues", data);
        logger.info("Actions Backup Created");
    }

    private static void restoreValues() {
        try {
            InputStream stream = fileService.getFile("temp/BackupValues"); // If file not found > AmazonS3Exception
            if (stream != null) {
                String values = IOUtils.toString(stream, StandardCharsets.UTF_8);
                logger.debug("Values To Restore: " + values);
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
            logger.error("Error During Restoring Actions", e);
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

    public static void getAll() {
        messages.sendMessageToAdmin(scheduledActionMapToString());
    }

    private static String scheduledActionMapToString() {
        logger.debug("Actions Map: " + scheduledActionMap);
        if (scheduledActionMap == null || scheduledActionMap.isEmpty()) {
            return "null";
        }
        StringBuilder data = new StringBuilder();
        for (Long key : scheduledActionMap.keySet()) {
            data.append(";").append(scheduledActionMap.get(key).toString());
        }
        logger.debug("Scheduled: " + data);
        data.deleteCharAt(0);
        return data.toString();
    }

    private static void stringToScheduledActionMap(String data) {
        scheduledActionMap = new HashMap<>();
        playerToScheduled = new HashMap<>();
        if (data == null) return;
        ArrayList<String> values = new ArrayList<>(Arrays.asList(data.split(";")));
        ScheduledAction action;
        ArrayList<Long> schedules;
        for (String value : values) {
            action = new ScheduledAction(value);
            scheduledActionMap.put(action.timestamp, action);
            if (playerToScheduled.containsKey(action.playerId)) {
                schedules = playerToScheduled.get(action.playerId);
                schedules.add(action.timestamp);
                playerToScheduled.put(action.playerId, schedules);
            }
        }
        startCheck();
    }

    private static String actionsToString() {
        logger.debug("Actions: " + actions);
        if (actions == null || actions.isEmpty()) {
            return "null";
        }
        StringBuilder data = new StringBuilder();
        for (String action : actions) {
            data.append(";").append(action);
        }
        logger.debug("Scheduled: " + data);
        data.deleteCharAt(0);
        return data.toString();
    }

    private static void stringToActions(String data) {
        actions = new ArrayList<>();
        if (data == null) return;
        actions.addAll(Arrays.asList(data.split(";")));
    }

    private static void check() {
        logger.debug("Checking Active Actions");
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
                    movement.sendLocationUpdate(scheduledActionMap.get(entry.getKey()));
                    playerId = entry.getValue().playerId;
                    playerActions = playerToScheduled.get(playerId);
                    playerActions.remove(entry.getValue().timestamp);
                    if (playerActions.isEmpty()) {
                        playerToScheduled.remove(playerId);
                    } else {
                        playerToScheduled.put(playerId, playerActions);
                    }
                    iterator.remove();
                    logger.debug("Action Finished");
                }
            }
            if (scheduledActionMap.isEmpty()) cancel();
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

    private static void schedule() {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }
        task = scheduledExecutorService.scheduleAtFixedRate(
                TimeDependentActions::check, 5, 5, TimeUnit.SECONDS);
    }

}