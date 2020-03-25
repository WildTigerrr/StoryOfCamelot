package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.FileLink;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Item;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemQuality;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemSubType;

import java.util.ArrayList;

public enum ItemsTemplate {
    SWORD_COMMON(
            10.0, 100, 10.0, ItemSubType.SWORD, ItemQuality.COMMON,
            NameTranslation.ITEM_SWORD_COMMON, FileLinkTemplate.FLYING_SWORD
    ),
    SWORD_UNCOMMON(
            15.0, 150, 25.0, ItemSubType.SWORD, ItemQuality.UNCOMMON,
            NameTranslation.ITEM_SWORD_UNCOMMON, FileLinkTemplate.FLYING_SWORD
    );

    private final double value;
    private final int durability;
    private final double price;
    private final ItemSubType subType;
    private final ItemQuality quality;
    private final NameTranslation nameTranslation;
    private final FileLinkTemplate imageLink;

    ItemsTemplate(double value, int durability, double price, ItemSubType subType, ItemQuality quality, NameTranslation translation, FileLinkTemplate imageLink) {
        this.value = value;
        this.durability = durability;
        this.price = price;
        this.subType = subType;
        this.quality = quality;
        nameTranslation = translation;
        this.imageLink = imageLink;
    }

    public double getValue() {
        return value;
    }

    public int getDurability() {
        return durability;
    }

    public double getPrice() {
        return price;
    }

    public ItemSubType getSubType() {
        return subType;
    }

    public ItemQuality getQuality() {
        return quality;
    }

    public NameTranslation getNameTranslation() {
        return nameTranslation;
    }

    public FileLink getFileLink() {
        if (imageLink == null) return null;
        return imageLink.getFileLink();
    }

    public static ArrayList<Item> getItems() {
        return new ArrayList<>() {
            {
                for (ItemsTemplate template : ItemsTemplate.values()) {
                    add(new Item(template));
                }
            }
        };
    }

}
