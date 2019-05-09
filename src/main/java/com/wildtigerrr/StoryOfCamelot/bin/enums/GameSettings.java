package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum GameSettings {
    DEFAULT_LOCATION (
            Emojis.CIRCUS_TENT.getCode() + " Торговая Площадь"
    ),
    FIRST_FOREST_LOCATION (
            Emojis.EVERGREEN_TREE.getCode() + " Лес"
    );

    private final String text;

    GameSettings(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }
}
