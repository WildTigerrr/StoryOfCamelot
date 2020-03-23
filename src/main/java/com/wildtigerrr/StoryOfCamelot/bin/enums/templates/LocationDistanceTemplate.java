package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import java.util.HashMap;

public enum LocationDistanceTemplate {
    TRADE_SQUARE_FOREST(
            LocationTemplate.TRADING_SQUARE.name(),
            LocationTemplate.FOREST.name(),
            10
    ),
    FOREST_TRADE_SQUARE(
            LocationTemplate.FOREST.name(),
            LocationTemplate.TRADING_SQUARE.name(),
            10
    ),
    FOREST_THICKET(
            LocationTemplate.FOREST.name(),
            LocationTemplate.THICKET.name(),
            30
    ),
    THICKET_FOREST(
            LocationTemplate.THICKET.name(),
            LocationTemplate.FOREST.name(),
            30
    ),
    FOREST_CAVE(
            LocationTemplate.FOREST.name(),
            LocationTemplate.CAVE.name(),
            5
    ),
    CAVE_FOREST(
            LocationTemplate.CAVE.name(),
            LocationTemplate.FOREST.name(),
            5
    );
    private final String start;
    private final String finish;
    private final Integer distance;

    LocationDistanceTemplate(String start, String finish, Integer distance) {
        this.start = start;
        this.finish = finish;
        this.distance = distance;
    }

    public String way() {
        return start + "*" + finish;
    }

    public Integer getDistance() {
        return distance;
    }

    public static HashMap<String, Integer> getLocationDistances() {
        return new HashMap<>() {{
            for (LocationDistanceTemplate template : LocationDistanceTemplate.values()) {
                put(template.way(), template.getDistance());
            }
        }};
    }
}
