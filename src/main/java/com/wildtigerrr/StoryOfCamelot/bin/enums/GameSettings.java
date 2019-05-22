package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum GameSettings {
    DEFAULT_LOCATION (
            NameTranslation.TRADING_SQUARE.getSystemName()
    ),
    FIRST_FOREST_LOCATION (
            NameTranslation.FOREST.getSystemName()
    );

    private final String text;

    GameSettings(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }
}
