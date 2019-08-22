package com.wildtigerrr.StoryOfCamelot.bin.enums;

import java.util.Locale;

public enum Language {
    ENG ( Emoji.FLAG_ENGLAND.getCode() + " English", Locale.ENGLISH),
    RUS ( Emoji.FLAG_RUSSIA.getCode() + " Русский", new Locale("RU")),
    UKR ( Emoji.FLAG_UKRAINE.getCode() + " Українська", new Locale("uk", "UA"));

    private final String name;
    private final Locale locale;

    Language(String name, Locale locale) {
        this.name = name;
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public Locale getLocale() {
        return locale;
    }

    public static Locale getDefaultLocale() {
        return RUS.getLocale();
    }
}