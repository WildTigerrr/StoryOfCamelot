package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum ReplyButtons {
    MOVE(Emojis.FOOTPRINTS.getCode() + "В путь"),
    ME(Emojis.SCROLL.getCode() + "Летопись");

    private final String label;

    ReplyButtons(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
