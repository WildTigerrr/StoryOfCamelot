package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.ActionType;

public class ScheduledAction {
    public ActionType type;
    public String target;
    public String additionalValue;
    public Long timestamp;
    public int playerId;

    ScheduledAction(Long timestamp, ActionType type, int playerId, String target) {
        this.timestamp = timestamp;
        this.type = type;
        this.playerId = playerId;
        this.target = target;
    }
    ScheduledAction(Long timestamp, ActionType type, int playerId, String target, String additionalValue) {
        this.timestamp = timestamp;
        this.type = type;
        this.playerId = playerId;
        this.target = target;
        this.additionalValue = additionalValue;
    }

}