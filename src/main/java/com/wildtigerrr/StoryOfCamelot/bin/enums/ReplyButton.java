package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum ReplyButton {
    MOVE(NameTranslation.BUTTON_MOVE),
    SKILLS(NameTranslation.BUTTON_SKILLS),
    ME(NameTranslation.BUTTON_ME);

    private final NameTranslation label;

    ReplyButton(NameTranslation label) {
        this.label = label;
    }

    public String getLabel(Language lang) {
        return label.getName(lang);
    }

    public static Command buttonToCommand(String text, Language lang) {
        if (text.equals(ReplyButton.MOVE.getLabel(lang))) return Command.MOVE;
        else if (text.equals(ReplyButton.ME.getLabel(lang))) return Command.ME;
        else if (text.equals(ReplyButton.SKILLS.getLabel(lang))) return Command.SKILLS;
        return null;
    }
}