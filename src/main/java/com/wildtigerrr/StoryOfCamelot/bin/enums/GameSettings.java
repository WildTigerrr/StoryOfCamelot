package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum GameSettings {
    DEFAULT_LOCATION (
            "Торговая Площадь"
    );

    private final String text;

    GameSettings(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }
}
