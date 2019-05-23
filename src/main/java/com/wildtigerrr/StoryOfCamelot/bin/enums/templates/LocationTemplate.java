package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.database.schema.FileLink;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;

import java.util.HashMap;

public enum LocationTemplate {
    FOREST(
            "forest",
            FileLinkTemplate.FOREST,
            false,
            NameTranslation.FOREST

    ),
    THICKET(
            "thicket",
            FileLinkTemplate.THICKET,
            false,
            NameTranslation.THICKET
    ),
    CAVE(
            "cave",
            null,
            false,
            NameTranslation.CAVE
    ),
    TRADING_SQUARE(
            "square",
            FileLinkTemplate.MERCHANTS_SQUARE,
            false,
            NameTranslation.TRADING_SQUARE
    );

    private final String systemName;
    private final FileLinkTemplate imageLink;
    private final Boolean hasStores;
    private final NameTranslation name;

    LocationTemplate(String systemName, FileLinkTemplate imageLink, Boolean hasStores, NameTranslation name) {
        this.systemName = systemName;
        this.imageLink = imageLink;
        this.hasStores = hasStores;
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

    public String systemName() {
        return systemName;
    }

    public static HashMap<String, Location> getLocations() {
        return new HashMap<String, Location>() {{
            for (LocationTemplate template : LocationTemplate.values()) {
                put(template.systemName(), new Location(template));
            }
        }};
    }
}
