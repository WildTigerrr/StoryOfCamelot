package com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums;

public enum ItemSubType {
    SWORD(ItemType.WEAPON);

    private final ItemType type;

    ItemSubType(ItemType type) {
        this.type = type;
    }
}
