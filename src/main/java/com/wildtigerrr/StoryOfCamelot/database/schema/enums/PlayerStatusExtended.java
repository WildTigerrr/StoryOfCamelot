package com.wildtigerrr.StoryOfCamelot.database.schema.enums;

public enum  PlayerStatusExtended {
    NONE(0),

    LANGUAGE_CHOOSE(1),
    TUTORIAL_NICKNAME(2),
    TUTORIAL_MOVEMENT(3),
    TUTORIAL_STATS(4),
    TUTORIAL_STATS_UP(5),
    TUTORIAL_STATS_UP_2(6),

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