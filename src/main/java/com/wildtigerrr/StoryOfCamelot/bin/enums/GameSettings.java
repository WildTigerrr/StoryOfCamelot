package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;

public enum GameSettings {
    DEFAULT_LOCATION (
            LocationTemplate.TRADING_SQUARE.systemName()
    ),
    FIRST_FOREST_LOCATION (
            LocationTemplate.FOREST.systemName()
    );

    private final String text;

    GameSettings(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }
}
