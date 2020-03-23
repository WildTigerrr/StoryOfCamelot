package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;

public enum ReplyButton {
    MOVE(NameTranslation.BUTTON_MOVE),
    SKILLS(NameTranslation.BUTTON_SKILLS),
    ME(NameTranslation.BUTTON_ME),
    FIGHT(NameTranslation.BUTTON_FIGHT);

    private final NameTranslation label;

    ReplyButton(NameTranslation label) {
        this.label = label;
    }

    public String getLabel(Language lang) {
        return label.getName(lang);
    }

    public String getLabel(Player player) {
        return getLabel(player.getLanguage());
    }

    public static Command buttonToCommand(String text, Language lang) {
        if (text.equals(ReplyButton.MOVE.getLabel(lang))) return Command.MOVE;
        else if (text.equals(ReplyButton.ME.getLabel(lang))) return Command.ME;
        else if (text.equals(ReplyButton.SKILLS.getLabel(lang))) return Command.SKILLS;
        else if (text.equals(ReplyButton.FIGHT.getLabel(lang))) return Command.FIGHT;
        return null;
    }
}