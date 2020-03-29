package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.FileLink;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Item;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemQuality;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemSubType;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.HashMap;

@Getter
public enum ItemTemplate {
    SWORD_COMMON(
            10.0, 100.0, 10.0, ItemSubType.SWORD, ItemQuality.COMMON,
            NameTranslation.ITEM_SWORD_COMMON, FileLinkTemplate.FLYING_SWORD
    ),
    SWORD_UNCOMMON(
            15.0, 150.0, 25.0, ItemSubType.SWORD, ItemQuality.UNCOMMON,
            NameTranslation.ITEM_SWORD_UNCOMMON, FileLinkTemplate.FLYING_SWORD
    );

    private final double value;
    private final double durability;
    private final double price;
    private final ItemSubType subType;
    private final ItemQuality quality;
    private final NameTranslation nameTranslation;
    @Getter(AccessLevel.NONE)
    private final FileLinkTemplate imageLink;

    ItemTemplate(double value, double durability, double price, ItemSubType subType, ItemQuality quality, NameTranslation translation, FileLinkTemplate imageLink) {
        this.value = value;
        this.durability = durability;
        this.price = price;
        this.subType = subType;
        this.quality = quality;
        nameTranslation = translation;
        this.imageLink = imageLink;
    }

    public FileLink getFileLink() {
        if (imageLink == null) return null;
        return imageLink.getFileLink();
    }

    public static HashMap<String, Item> getItems() {
        return new HashMap<>() {{
            for (ItemTemplate template : ItemTemplate.values()) {
                put(template.name(), new Item(template));
            }
        }};
    }

}
