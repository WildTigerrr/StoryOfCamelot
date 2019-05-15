package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum ReplyButton {
    MOVE(Emoji.FOOTPRINTS.getCode() + "В путь"),
    SKILLS(Emoji.SKILLS.getCode() + "Навыки"),
    ME(Emoji.SCROLL.getCode() + "Летопись");

    private final String label;

    ReplyButton(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Command buttonToCommand(String text) {
        if (text.equals(ReplyButton.MOVE.getLabel())) return Command.MOVE;
        else if (text.equals(ReplyButton.ME.getLabel())) return Command.ME;
        else if (text.equals(ReplyButton.SKILLS.getLabel())) return Command.SKILLS;
        return null;
    }
}