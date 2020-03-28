package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import lombok.Getter;

@Getter
public enum DropTemplate {

    FLYING_SWORD_SWORD(MobTemplate.FLYING_SWORD, ItemTemplate.SWORD_COMMON),
    SUPER_FLYING_SWORD_SWORD(MobTemplate.SUPER_FLYING_SWORD, ItemTemplate.SWORD_UNCOMMON);

    private final MobTemplate mob;
    private final ItemTemplate item;

    DropTemplate(MobTemplate mob, ItemTemplate item) {
        this.mob = mob;
        this.item = item;
    }

    public String getMobName() {
        return mob.name();
    }

    public String getItemName() {
        return item.name();
    }

}
