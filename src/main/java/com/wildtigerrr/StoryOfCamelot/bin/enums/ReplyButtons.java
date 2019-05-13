package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum ReplyButtons {
    MOVE(Emojis.FOOTPRINTS.getCode() + "В путь"),
    SKILLS(Emojis.SKILLS.getCode() + "Навыки"),
    ME(Emojis.SCROLL.getCode() + "Летопись");

    private final String label;

    ReplyButtons(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Command buttonToCommand(String text) {
        if (text.equals(ReplyButtons.MOVE.getLabel())) return Command.MOVE;
        else if (text.equals(ReplyButtons.ME.getLabel())) return Command.ME;
        else if (text.equals(ReplyButtons.SKILLS.getLabel())) return Command.SKILLS;
        return null;
    }
}