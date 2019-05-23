package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import java.util.HashMap;

public enum LocationDistanceTemplate {
    TRADESQUARE_FOREST(
            LocationTemplate.TRADING_SQUARE.systemName(),
            LocationTemplate.FOREST.systemName(),
            10
    ),
    FOREST_TRADESQUARE(
            LocationTemplate.FOREST.systemName(),
            LocationTemplate.TRADING_SQUARE.systemName(),
            10
    ),
    FOREST_THICKET(
            LocationTemplate.FOREST.systemName(),
            LocationTemplate.THICKET.systemName(),
            30
    ),
    THICKET_FOREST(
            LocationTemplate.THICKET.systemName(),
            LocationTemplate.FOREST.systemName(),
            30
    ),
    FOREST_CAVE(
            LocationTemplate.FOREST.systemName(),
            LocationTemplate.CAVE.systemName(),
            5
    ),
    CAVE_FOREST(
            LocationTemplate.CAVE.systemName(),
            LocationTemplate.FOREST.systemName(),
            5
    )
    ;
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
        return new HashMap<String, Integer>() {{
            for (LocationDistanceTemplate template : LocationDistanceTemplate.values()) {
                put(template.way(), template.getDistance());
            }
        }};
    }
}
