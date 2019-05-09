package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.vdurmont.emoji.EmojiParser;

public enum GameSettings {
    DEFAULT_LOCATION (
            Emojis.CIRCUS_TENT.getCode() + " Торговая Площадь"
    ),
    FIRST_FOREST_LOCATION (
            Emojis.EVRGREEN_TREE.getCode() + " Лес"
    );

    private final String text;

    GameSettings(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }
}
