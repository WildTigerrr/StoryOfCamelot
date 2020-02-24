package com.wildtigerrr.StoryOfCamelot.database.schema.enums;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Emoji;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;

public enum Stats {
    STRENGTH(
            NameTranslation.SKILL_STRENGTH_WHICH,
            NameTranslation.SKILL_STRENGTH_WHAT,
            Emoji.STRENGTH.getCode(), "S"
    ),
    HEALTH(
            NameTranslation.SKILL_HEALTH_WHICH,
            NameTranslation.SKILL_HEALTH_WHAT,
            Emoji.HEALTH.getCode(), "H"
    ),
    AGILITY(
            NameTranslation.SKILL_AGILITY_WHICH,
            NameTranslation.SKILL_AGILITY_WHAT,
            Emoji.AGILITY.getCode(), "A"
    ),
    CHARISMA(
            NameTranslation.SKILL_CHARISMA_WHICH,
            NameTranslation.SKILL_CHARISMA_WHAT,
            Emoji.CHARISMA.getCode(), "C"
    ),
    INTELLIGENCE(
            NameTranslation.SKILL_INTELLIGENCE_WHICH,
            NameTranslation.SKILL_INTELLIGENCE_WHAT,
            Emoji.INTELLIGENCE.getCode(), "I"
    ),
    ENDURANCE(
            NameTranslation.SKILL_ENDURANCE_WHICH,
            NameTranslation.SKILL_ENDURANCE_WHAT,
            Emoji.ENDURANCE.getCode(), "E"
    ),
    LUCK(
            NameTranslation.SKILL_LUCK_WHICH,
            NameTranslation.SKILL_LUCK_WHAT,
            Emoji.LUCK.getCode(), "L"
    );


    private final NameTranslation which;
    private final NameTranslation what;
    private final String emoji;
    private final String character;

    Stats(NameTranslation which, NameTranslation what, String emoji, String character) {
        this.which = which;
        this.what = what;
        this.emoji = emoji;
        this.character = character;
    }

    public String emoji() {
        return emoji;
    }

    public String which(Language lang) {
        return which.getName(lang);
    }
    public String whichLowercase(Language lang) {
        return which.getName(lang).toLowerCase();
    }

    public String what(Language lang) {
        return what.getName(lang);
    }
    public String whatLowercase(Language lang) {
        return what.getName(lang).toLowerCase();
    }

    public String getCharacter() {
        return character;
    }

    public Boolean containsExperience() {
        return this == Stats.LUCK;
    }

    public static Stats getStat(String character) {
        character = character.toUpperCase();
        switch (character) {
            case "S":
                return STRENGTH;
            case "H":
                return HEALTH;
            case "A":
                return AGILITY;
            case "C":
                return CHARISMA;
            case "I":
                return INTELLIGENCE;
            case "E":
                return ENDURANCE;
            case "L":
                return LUCK;
            default:
                return null;
        }
    }

}
