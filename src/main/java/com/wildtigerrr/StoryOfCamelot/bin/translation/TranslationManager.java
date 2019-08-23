package com.wildtigerrr.StoryOfCamelot.bin.translation;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Locale;

@Service
public class TranslationManager {

    @Autowired
    private MessageSource messageSource;

    private TranslationEng translationEng;
    private TranslationUkr translationUkr;
    private TranslationRus translationRus;

    @PostConstruct
    private void initializeLanguages() {
        this.translationEng = new TranslationEng();
        this.translationUkr = new TranslationUkr();
        this.translationRus = new TranslationRus();
    }

    public Translation get(Language lang) {
        switch (lang) {
            case ENG: return translationEng;
            case UKR: return translationUkr;
            default: return translationRus;
        }
    }

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, "Oops!", Language.getDefaultLocale());
    }

    public String getMessage(String code, Language lang) {
        return getMessage(code, lang.getLocale());
    }

    public String getMessage(String code, Locale locale) {
        return getMessage(code, locale, null);
    }

    public String getMessage(String code, Language lang, Object[] args) {
        return getMessage(code, lang.getLocale(), args);
    }

    public String getMessage(String code, Locale locale, Object[] args) {
        return messageSource.getMessage(code, args, "Oops!", locale);
    }

}
