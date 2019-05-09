package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.vdurmont.emoji.EmojiParser;

public enum GameSettings {
    DEFAULT_LOCATION (
            EmojiParser.parseToUnicode(":circus_tent:") + " Торговая Площадь"
    ),
    FIRST_FOREST_LOCATION (
            EmojiParser.parseToUnicode(":evergreen_tree:") + " Лес"
    );

    private final String text;

    GameSettings(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }
}
