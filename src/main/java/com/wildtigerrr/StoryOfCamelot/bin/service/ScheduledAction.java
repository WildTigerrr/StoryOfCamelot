package com.wildtigerrr.StoryOfCamelot.bin.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.ActionType;

import java.util.HashMap;

public class ScheduledAction {
    public ActionType type;
    public String target;
    public String additionalValue;
    public Long timestamp;
    public int playerId;

    public ScheduledAction(Long timestamp, ActionType type, int playerId, String target) {
        this.type = type;
        this.target = target;
        this.timestamp = timestamp;
        this.playerId = playerId;
    }
    public ScheduledAction(Long timestamp, ActionType type, int playerId, String target, String additionalValue) {
        this.type = type;
        this.target = target;
        this.additionalValue = additionalValue;
        this.timestamp = timestamp;
        this.playerId = playerId;
    }
    public ScheduledAction(String stringifiedAction) {
        if (stringifiedAction == null || stringifiedAction.length() == 0 || !stringifiedAction.contains("{") || !stringifiedAction.contains("}")) {
            return;
        }
        stringifiedAction = stringifiedAction.substring(stringifiedAction.indexOf("{") + 1);
        stringifiedAction = stringifiedAction.substring(0, stringifiedAction.indexOf("}"));

        HashMap<String, String> actionValues = parse(stringifiedAction);

        this.type = ActionType.valueOf(actionValues.get("type"));
        this.target = actionValues.get("target").replace("\'", "");
        this.additionalValue = actionValues.get("additionalValue").replace("\'", "");
        this.timestamp = Long.valueOf(actionValues.get("timestamp"));
        this.playerId = Integer.valueOf(actionValues.get("playerId"));

        System.out.println(stringifiedAction);
    }

    private HashMap<String, String> parse(String action) {
        HashMap<String, String> actionValues = new HashMap<>();
        String[] values;
        for (String str : action.split("\\|")) {
            values = str.split("=", 2);
            if (values.length == 2) {
                actionValues.put(values[0], values[1]);
            } else {
                actionValues.put(values[0], null);
            }
        }
        return actionValues;
    }

    @Override
    public String toString() {
        return "{" +
                "type=" + type.name() +
                "|target='" + target + '\'' +
                "|additionalValue='" + additionalValue + '\'' +
                "|timestamp=" + timestamp +
                "|playerId=" + playerId +
                '}';
    }
}