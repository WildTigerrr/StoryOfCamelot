package com.wildtigerrr.StoryOfCamelot.database.schema.enums;

public enum  PlayerStatusExtended {
    NONE(0),

    TUTORIAL_NICKNAME(1),
    TUTORIAL_MOVEMENT(2),
    TUTORIAL_STATS(3),
    TUTORIAL_STATS_UP(4),

    BAN_DAY(101),
    BAN_WEEK(102),
    BAN_MONTH(103),
    BAN_PERMANENT(104);

    private int number;

    PlayerStatusExtended(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}