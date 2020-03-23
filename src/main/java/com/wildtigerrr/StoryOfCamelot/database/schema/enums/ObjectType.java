package com.wildtigerrr.StoryOfCamelot.database.schema.enums;

import lombok.Getter;

public enum ObjectType {

    BACKPACK(
            "", "a0b0"
    ),
    FILE_LINK(
            "", "a0f0"
    ),
    ITEM(
            "", "a0i0"
    ),
    LOCATION(
            "", "a0l0"
    ),
    LOCATION_NEAR(
            "", "a0ln"
    ),
    MOB(
            "", "a0m0"
    ),
//    MOB_DROP(
//            "", "a0md"
//    ),
    NPC(
            "", "a0n0"
    ),
    PLAYER(
            "player_seq", "a0p0"
    ),
    POSSIBLE_LOCATION(
            "", "a0pl"
    );

    private final String sequenceName;
    private final String prefix;

    ObjectType(String sequenceName, String prefix) {
        this.sequenceName = sequenceName;
        this.prefix = prefix;
    }

    public String sequenceName() {
        return sequenceName;
    }

    public String prefix() {
        return prefix;
    }

}
