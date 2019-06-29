package com.wildtigerrr.StoryOfCamelot.bin.translation;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TranslationManager {

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

}
