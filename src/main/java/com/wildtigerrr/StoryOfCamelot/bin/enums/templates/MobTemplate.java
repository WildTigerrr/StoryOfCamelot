package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.FileLink;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;

import java.util.HashMap;

public enum MobTemplate {
    FLYING_SWORD(
            NameTranslation.MOB_FLYING_SWORD,
            1, 2, 5, 0, 0,
            FileLinkTemplate.FLYING_SWORD
    ),
    SUPER_FLYING_SWORD(
            NameTranslation.MOB_SUPER_FLYING_SWORD,
            2, 3, 7, 2, 2,
            FileLinkTemplate.FLYING_SWORD
    );

    private final NameTranslation name;
    private final int level;
    private final int damage;
    private final int hitpoints;
    private final int defence;
    private final int agility;
    private final FileLinkTemplate imageLink;

    MobTemplate(NameTranslation name, int level, int damage, int hitpoints, int defence, int agility, FileLinkTemplate imageLink) {
        this.name = name;
        this.level = level;
        this.damage = damage;
        this.hitpoints = hitpoints;
        this.defence = defence;
        this.agility = agility;
        this.imageLink = imageLink;
    }

    public NameTranslation getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getDamage() {
        return damage;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public int getDefence() {
        return defence;
    }

    public int getAgility() {
        return agility;
    }

    public FileLink getFileLink() {
        if (imageLink == null) return null;
        return imageLink.getFileLink();
    }

    public static HashMap<String, Mob> getMobs() {
        return new HashMap<>() {{
            for (MobTemplate template : MobTemplate.values()) {
                put(template.name(), new Mob(template));
            }
        }};
    }
}