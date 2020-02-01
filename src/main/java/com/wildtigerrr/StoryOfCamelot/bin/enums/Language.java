package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

public enum Language {
    ENG(Emoji.FLAG_ENGLAND.getCode(), "language.eng", Locale.ENGLISH),
    RUS(Emoji.FLAG_RUSSIA.getCode(), "language.rus", new Locale("ru", "RU")),
    UKR(Emoji.FLAG_UKRAINE.getCode(), "language.ukr", new Locale("uk", "UA"));

    private final String name;
    private final String namePath;
    private final Locale locale;

    private static TranslationManager translations;

    Language(String name, String namePath, Locale locale) {
        this.name = name;
        this.namePath = namePath;
        this.locale = locale;
    }

    public String getName() {
        return name + " " + translations.getMessage(namePath);
    }

    public Locale getLocale() {
        return locale;
    }

    public static Locale getDefaultLocale() {
        return RUS.getLocale();
    }

    public static boolean isValidLanguageCode(String languageCode) {
        return StringUtils.isNumeric(languageCode) && (Integer.parseInt(languageCode) < Language.values().length);
    }

    // List - https://datahub.io/core/language-codes/r/3.html
    public static Language byCountryCode(String countryCode) {
        if (countryCode.equals("ru")) return Language.RUS;
        else if (countryCode.equals("uk")) return Language.UKR;
        else return Language.ENG;
    }

    @Component
    public static class TranslationInjector {
        private final TranslationManager translations;

        public TranslationInjector(TranslationManager translations) {
            this.translations = translations;
        }

        @PostConstruct
        public void postConstruct() {
            Language.setTranslationManager(translations);
        }
    }

    private static void setTranslationManager(TranslationManager translationManager) {
        translations = translationManager;
    }
}