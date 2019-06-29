package com.wildtigerrr.StoryOfCamelot.database.schema.enums;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Emoji;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;

public enum Stats {
    STRENGTH(
            NameTranslation.SKILL_STRENGTH_WHICH,
            NameTranslation.SKILL_STRENGTH_WHAT,
            Emoji.STRENGTH.getCode(), "S",
            "strength", "strengthExp"
    ),
    HEALTH(
            NameTranslation.SKILL_HEALTH_WHICH,
            NameTranslation.SKILL_HEALTH_WHAT,
            Emoji.HEALTH.getCode(), "H",
            "health", "healthExp"
    ),
    AGILITY(
            NameTranslation.SKILL_AGILITY_WHICH,
            NameTranslation.SKILL_AGILITY_WHAT,
            Emoji.AGILITY.getCode(), "A",
            "agility", "agilityExp"
    ),
    CHARISMA(
            NameTranslation.SKILL_CHARISMA_WHICH,
            NameTranslation.SKILL_CHARISMA_WHAT,
            Emoji.CHARISMA.getCode(), "C",
            "charisma", "charismaExp"
    ),
    INTELLIGENCE(
            NameTranslation.SKILL_INTELLIGENCE_WHICH,
            NameTranslation.SKILL_INTELLIGENCE_WHAT,
            Emoji.INTELLIGENCE.getCode(), "I",
            "intelligence", "intelligenceExp"
    ),
    ENDURANCE(
            NameTranslation.SKILL_ENDURANCE_WHICH,
            NameTranslation.SKILL_ENDURANCE_WHAT,
            Emoji.ENDURANCE.getCode(), "E",
            "endurance", "enduranceExp"
    ),
    LUCK(
            NameTranslation.SKILL_LUCK_WHICH,
            NameTranslation.SKILL_LUCK_WHAT,
            Emoji.LUCK.getCode(), "L",
            "luck", null
    );


    private final NameTranslation which;
    private final NameTranslation what;
    private final String emoji;
    private final String character;
    private final String fieldName;
    private final String expFieldName;

    Stats(NameTranslation which, NameTranslation what, String emoji, String character, String fieldName, String expFieldName) {
        this.which = which;
        this.what = what;
        this.emoji = emoji;
        this.character = character;
        this.fieldName = fieldName;
        this.expFieldName = expFieldName;
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
