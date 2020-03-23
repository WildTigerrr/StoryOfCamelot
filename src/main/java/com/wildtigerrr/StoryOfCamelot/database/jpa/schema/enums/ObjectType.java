package com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums;

public enum ObjectType {

    BACKPACK(
            "backpack_seq", "a0b0"
    ),
    FILE_LINK(
            "file_link_seq", "a0f0"
    ),
    ITEM(
            "item_seq", "a0i0"
    ),
    LOCATION(
            "location_seq", "a0l0"
    ),
    LOCATION_NEAR(
            "location_near_seq", "a0ln"
    ),
    LOCATION_POSSIBLE(
            "location_possible_seq", "a0pl"
    ),
    MOB(
            "mob_seq", "a0m0"
    ),
    MOB_DROP(
            "mob_drop_seq", "a0md"
    ),
    NPC(
            "npc_seq", "a0n0"
    ),
    PLAYER(
            "player_seq", "a0p0"
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
