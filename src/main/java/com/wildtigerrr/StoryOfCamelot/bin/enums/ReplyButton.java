package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import lombok.Getter;

public enum ReplyButton {
    MOVE(NameTranslation.BUTTON_MOVE, Command.MOVE),
    SKILLS(NameTranslation.BUTTON_SKILLS, Command.SKILLS),
    LEVEL_UP(NameTranslation.BUTTON_LEVEL_UP, Command.SKILL_LEARN),
    ME(NameTranslation.BUTTON_ME, Command.ME),
    FIGHT(NameTranslation.BUTTON_FIGHT, Command.FIGHT),
    SEARCH_ENEMIES(NameTranslation.BUTTON_SEARCH_ENEMIES, Command.SEARCH_ENEMIES),
    BACK(NameTranslation.BUTTON_BACK, Command.BACK),
    PLAYERS_TOP(NameTranslation.BUTTON_PLAYERS_TOP, Command.PLAYERS_TOP),
    BACKPACK(NameTranslation.BUTTON_BACKPACK, Command.BACKPACK),
    STORES(NameTranslation.BUTTON_STORES, Command.STORES),

    SKIP_LINE(NameTranslation.BUTTON_BACK, Command.DEFAULT),

    STORE_MERCHANT(NameTranslation.STORE_MERCHANT, Command.STORE_SELECT),
    STORE_GROCERY(NameTranslation.STORE_GROCERY, Command.STORE_SELECT),

    // Skills
    FIGHT_ATTACK(Skill.BASIC_ATTACK),
    FIGHT_STRONG_ATTACK(Skill.STRONG_ATTACK),
    FIGHT_ADVANCED_STRONG_ATTACK(Skill.ADVANCED_STRONG_ATTACK),
    FIGHT_FAST_ATTACK(Skill.FAST_ATTACK),
    FIGHT_DEFENCE(Skill.BASIC_DEFENCE),;

    private final NameTranslation label;
    @Getter
    private final Command command;
    @Getter
    private final Skill skill;

    ReplyButton(NameTranslation label, Command command) {
        this.label = label;
        this.command = command;
        this.skill = null;
    }

    ReplyButton(Skill skill) {
        this.label = skill.getLabelTranslations();
        this.command = Command.FIGHT;
        this.skill = skill;
    }

    public String getLabel(Language lang) {
        return label.getName(lang);
    }

    public String getLabel(Player player) {
        return getLabel(player.getLanguage());
    }

    public Boolean isSkip() {
        return this == SKIP_LINE;
    }

    public Boolean hasSkill() {
        return skill != null;
    }

    public static ReplyButton getButton(String text, Language lang) {
        for (ReplyButton button : ReplyButton.values()) {
            if (text.equals(button.getLabel(lang))) return button;
        }
        return ReplyButton.BACK;
    }

    public static Command buttonToCommand(String text, Language lang) {
        return getButton(text, lang).getCommand();
    }

    public static Skill buttonToSkill(String text, Language lang) {
        return getButton(text, lang).getSkill();
    }

    public static ReplyButton skillToButton(Skill skill) {
        for (ReplyButton button : ReplyButton.values()) {
            if (button.hasSkill() && button.getSkill() == skill) return button;
        }
        return ReplyButton.FIGHT_ATTACK;
    }

}