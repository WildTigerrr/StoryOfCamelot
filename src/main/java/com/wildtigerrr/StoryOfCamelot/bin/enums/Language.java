package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum Language {
    RUS ( Emoji.FLAG_RUSSIA + " Русский"),
    UKR ( Emoji.FLAG_UKRAINE + " Українська");

    private final String name;

    Language(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}