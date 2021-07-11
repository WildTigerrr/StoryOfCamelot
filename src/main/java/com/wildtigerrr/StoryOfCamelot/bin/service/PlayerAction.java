package com.wildtigerrr.StoryOfCamelot.bin.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.PlayerActionType;

public class PlayerAction {

    public final PlayerActionType type;
    public final String value;

    public PlayerAction(PlayerActionType type, String value) {
        this.type = type;
        this.value = value;
    }

}
