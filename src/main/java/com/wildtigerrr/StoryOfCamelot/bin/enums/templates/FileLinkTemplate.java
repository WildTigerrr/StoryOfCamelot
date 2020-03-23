package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.FileLink;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public enum FileLinkTemplate {
    // LOCATIONS
    FOREST(
            "images/locations/forest-test.png"
    ),
    THICKET(
            "images/locations/thicket.png"
    ),
    MERCHANTS_SQUARE(
            "images/locations/the-merchants-square.png"
    ),

    // MOBS
    FLYING_SWORD(
            "images/items/weapons/swords/sword-test.png"
    );

    private FileLink fileLink;

    FileLinkTemplate(@NotNull String link) {
        this.fileLink = new FileLink(this.name(), link);
    }

    public void setFileLink(FileLink link) {
        fileLink = link;
    }

    public FileLink getFileLink() {
        return fileLink;
    }

    public static ArrayList<FileLink> getFileLinks() {
        return new ArrayList<FileLink>() {
            {
                for (FileLinkTemplate template : FileLinkTemplate.values()) {
                    add(template.getFileLink());
                }
            }
        };
    }
}
