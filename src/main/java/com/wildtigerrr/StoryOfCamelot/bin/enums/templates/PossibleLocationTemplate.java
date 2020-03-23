package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import java.util.ArrayList;
import java.util.HashMap;

public enum PossibleLocationTemplate {
    FOREST_MOBS(
            LocationTemplate.FOREST.name(),
            new ArrayList<>() {
                {
                    add(MobTemplate.FLYING_SWORD.name());
                }
            }
    ),
    THICKET_MOBS(
            LocationTemplate.THICKET.name(),
            new ArrayList<>() {
                {
                    add(MobTemplate.FLYING_SWORD.name());
                    add(MobTemplate.SUPER_FLYING_SWORD.name());
                }
            }
    );

    private final String location;
    private final ArrayList<String> mobs;

    PossibleLocationTemplate(String location, ArrayList<String> mobs) {
        this.location = location;
        this.mobs = mobs;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList<String> getMobs() {
        return mobs;
    }

    public static HashMap<String, ArrayList<String>> getPossibleLocationsMapping() {
        return new HashMap<>() {
            {
                for (PossibleLocationTemplate template : PossibleLocationTemplate.values()) {
                    put(template.getLocation(), template.getMobs());
                }
            }
        };
    }
}
