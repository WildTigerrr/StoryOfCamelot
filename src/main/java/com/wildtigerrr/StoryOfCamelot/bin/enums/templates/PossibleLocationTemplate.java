package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
public enum PossibleLocationTemplate {
    FOREST_MOBS(
            LocationTemplate.FOREST.name(),
            new HashMap<>() {
                {
                    put(MobTemplate.FLYING_SWORD.name(), 100);
                }
            }
    ),
    THICKET_MOBS(
            LocationTemplate.THICKET.name(),
            new HashMap<>() {
                {
                    put(MobTemplate.FLYING_SWORD.name(), 90);
                    put(MobTemplate.SUPER_FLYING_SWORD.name(), 10);
                }
            }
    );

    private final String location;
    private final HashMap<String, Integer> mobs;

    PossibleLocationTemplate(String location, HashMap<String, Integer> mobs) {
        this.location = location;
        this.mobs = mobs;
    }

    public static HashMap<String, HashMap<String, Integer>> getPossibleLocationsMapping() {
        return new HashMap<>() {
            {
                for (PossibleLocationTemplate template : PossibleLocationTemplate.values()) {
                    put(template.getLocation(), template.getMobs());
                }
            }
        };
    }
}
