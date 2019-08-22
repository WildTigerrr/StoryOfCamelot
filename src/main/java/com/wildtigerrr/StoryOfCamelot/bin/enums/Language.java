package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public enum Language {
    ENG(Emoji.FLAG_ENGLAND.getCode(), "language.eng", Locale.ENGLISH),
    RUS(Emoji.FLAG_RUSSIA.getCode(), "language.rus", new Locale("ru", "RU")),
    UKR(Emoji.FLAG_UKRAINE.getCode(), "language.ukr", new Locale("uk", "UA"));

    @Autowired
    private TranslationManager translations;

    private final String smile;
    private final String namePath;
    private final Locale locale;

    Language(String smile, String namePath, Locale locale) {
        this.smile = smile;
        this.namePath = namePath;
        this.locale = locale;
    }

    public String getName() {
        return smile + " " + translations.getMessage(namePath);
    }

    public Locale getLocale() {
        return locale;
    }

    public static Locale getDefaultLocale() {
        return RUS.getLocale();
    }
}