package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

public enum NameTranslation {
    // LOCATIONS
    LOC_FOREST(
            Emoji.EVERGREEN_TREE.getCode() + " ",
            "location.wild.forest"
    ),
    LOC_THICKET(
            Emoji.EVERGREEN_TREE.getCode() + " ",
            "location.wild.thicket"
    ),
    LOC_CAVE(
            Emoji.BAT.getCode() + " ",
            "location.cave.mythical"
    ),
    LOC_TRADING_SQUARE(
            Emoji.CIRCUS_TENT.getCode() + " ",
            "location.town.merchant-square"
    ),


    // MOBS
    MOB_FLYING_SWORD(
            "mob.aggressive.flying-sword"
    ),
    MOB_SUPER_FLYING_SWORD(
            "mob.aggressive.super-flying-sword"
    ),


    // BUTTONS
    BUTTON_MOVE(
            Emoji.FOOTPRINTS.getCode(), "button.move"
    ),
    BUTTON_SKILLS(
            Emoji.SKILLS.getCode(), "button.skills"
    ),
    BUTTON_ME(
            Emoji.SCROLL.getCode(), "button.me"
    ),
    BUTTON_FIGHT(
            Emoji.FIGHT.getCode(), "button.fight"
    ),


    // SKILLS
    SKILL_STRENGTH_WHAT(
            "skill.name.what.strength"
    ),
    SKILL_STRENGTH_WHICH(
            "skill.name.which.strength"
    ),
    SKILL_HEALTH_WHAT(
            "skill.name.what.health"
    ),
    SKILL_HEALTH_WHICH(
            "skill.name.which.health"
    ),
    SKILL_AGILITY_WHAT(
            "skill.name.what.agility"
    ),
    SKILL_AGILITY_WHICH(
            "skill.name.which.agility"
    ),
    SKILL_CHARISMA_WHAT(
            "skill.name.what.charisma"
    ),
    SKILL_CHARISMA_WHICH(
            "skill.name.which.charisma"
    ),
    SKILL_INTELLIGENCE_WHAT(
            "skill.name.what.intelligence"
    ),
    SKILL_INTELLIGENCE_WHICH(
            "skill.name.which.intelligence"
    ),
    SKILL_ENDURANCE_WHAT(
            "skill.name.what.endurance"
    ),
    SKILL_ENDURANCE_WHICH(
            "skill.name.which.endurance"
    ),
    SKILL_LUCK_WHAT(
            "skill.name.what.luck"
    ),
    SKILL_LUCK_WHICH(
            "skill.name.which.luck"
    )
    ;

    private final String name;
    private final String namePath;

    private static TranslationManager translations;

    NameTranslation(String name, String namePath) {
        this.name = name;
        this.namePath = namePath;
    }

    NameTranslation(String namePath) {
        this.name = "";
        this.namePath = namePath;
    }

    public String getName(Language lang) {
        return name + translations.getMessage(namePath, lang);
    }

    @Component
    public static class TranslationInjector {
        @Autowired
        private TranslationManager translations;
        @PostConstruct
        public void postConstruct() {
            NameTranslation.setTranslationManager(translations);
        }
    }

    private static void setTranslationManager(TranslationManager translationManager) {
        translations = translationManager;
    }
}