package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    // STORES
    STORE_MERCHANT(
            "store.name.merchant"
    ),
    STORE_GROCERY(
            "store.name.grocery"
    ),


    // MOBS
    MOB_FLYING_SWORD(
            "mob.aggressive.flying-sword"
    ),
    MOB_SUPER_FLYING_SWORD(
            "mob.aggressive.super-flying-sword"
    ),

    // ITEMS
    ITEM_WEAPON_SWORD_COMMON(
            "item.weapon.sword.common"
    ),
    ITEM_WEAPON_SWORD_UNCOMMON(
            "item.weapon.sword.uncommon"
    ),
    ITEM_MATERIAL_STICK(
            "item.material.stick"
    ),
    ITEM_MATERIAL_STONE(
            "item.material.stone"
    ),

    // BUTTONS
    BUTTON_MOVE(
            Emoji.FOOTPRINTS.getCode(), "button.move"
    ),
    BUTTON_SKILLS(
            Emoji.SKILLS.getCode(), "button.skills"
    ),
    BUTTON_LEVEL_UP(
            Emoji.CROWN.getCode(), "button.level_up"
    ),
    BUTTON_ME(
            Emoji.SCROLL.getCode(), "button.me"
    ),
    BUTTON_PLAYERS_TOP(
            Emoji.CROWN.getCode(), "button.players_top"
    ),
    BUTTON_FIGHT(
            Emoji.FIGHT.getCode(), "button.fight"
    ),
    BUTTON_SEARCH_ENEMIES(
            Emoji.FIGHT.getCode(), "button.search_enemies"
    ),
    BUTTON_BACK(
            Emoji.BACK.getCode(), "button.back"
    ),
    BUTTON_BACKPACK(
            Emoji.BACKPACK.getCode(), "button.backpack"
    ),
    BUTTON_STORES(
            Emoji.STORE.getCode(), "button.stores"
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
    ),

    SKILL_FIGHT_ATTACK(
            Emoji.FIGHT.getCode(), "skill.fight.base_attack"
    ),
    SKILL_FIGHT_STRONG_ATTACK(
            Emoji.STRENGTH.getCode(), "skill.fight.strong_attack"
    ),
    SKILL_FIGHT_ADVANCED_STRONG_ATTACK(
            Emoji.STRENGTH.getCode(), "skill.fight.advanced_strong_attack"
    ),
    SKILL_FIGHT_FAST_ATTACK(
            Emoji.DAGGER.getCode(), "skill.fight.fast_attack"
    ),
    SKILL_FIGHT_DEFENCE(
            Emoji.SHIELD.getCode(), "skill.fight.base_defence"
    ),

    // DESCRIPTIONS
    DESC_ITEM_SWORD_COMMON("description.item.sword.common"),
    DESC_ITEM_SWORD_UNCOMMON("description.item.sword.uncommon"),
    DESC_ITEM_STICK("description.item.craft.stick"),
    DESC_ITEM_STONE("description.item.craft.stone"),

    DESC_SKILL_ADVANCED_STRONG_ATTACK("description.skill.advanced_strong_attack"),
    DESC_SKILL_FAST_ATTACK("description.skill.fast_attack"),
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

    public String getName(Player player) {
        return getName(player.getLanguage());
    }

    @Component
    public static class TranslationInjector {
        private final TranslationManager translations;

        public TranslationInjector(TranslationManager translations) {
            this.translations = translations;
        }

        @PostConstruct
        public void postConstruct() {
            NameTranslation.setTranslationManager(translations);
        }
    }

    private static void setTranslationManager(TranslationManager translationManager) {
        translations = translationManager;
    }
}