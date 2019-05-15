package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum Language {
    RUS ( Emoji.FLAG_RUSSIA.getCode() + " Русский"),
    UKR ( Emoji.FLAG_UKRAINE.getCode() + " Українська");

    private final String name;

    Language(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}