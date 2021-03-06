package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.FileLink;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;

import java.util.HashMap;

public enum LocationTemplate {
    FOREST(
            FileLinkTemplate.FOREST,
            false, true,
            NameTranslation.LOC_FOREST

    ),
    THICKET(
            FileLinkTemplate.THICKET,
            false, true,
            NameTranslation.LOC_THICKET
    ),
    CAVE(
            null,
            false, true,
            NameTranslation.LOC_CAVE
    ),
    TRADING_SQUARE(
            FileLinkTemplate.MERCHANTS_SQUARE,
            true, false,
            NameTranslation.LOC_TRADING_SQUARE
    );

    private final FileLinkTemplate imageLink;
    private final Boolean hasStores;
    private final Boolean hasEnemies;
    private final NameTranslation name;

    LocationTemplate(FileLinkTemplate imageLink, Boolean hasStores, Boolean hasEnemies, NameTranslation name) {
        this.imageLink = imageLink;
        this.hasStores = hasStores;
        this.hasEnemies = hasEnemies;
        this.name = name;
    }

    public NameTranslation getTranslations() {
        return name;
    }

    public FileLink getFileLink() {
        if (imageLink == null) return null;
        return imageLink.getFileLink();
    }

    public Boolean hasStores() {
        return hasStores;
    }

    public Boolean hasEnemies() {
        return hasEnemies;
    }

    public static HashMap<String, Location> getLocations() {
        return new HashMap<>() {{
            for (LocationTemplate template : LocationTemplate.values()) {
                put(template.name(), new Location(template));
            }
        }};
    }
}
