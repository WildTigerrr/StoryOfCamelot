package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.FileLink;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Item;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemQuality;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemSubType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.StoreType;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Getter
public enum ItemTemplate {
    SWORD_COMMON(
            10.0, 100.0, 10, false, ItemSubType.SWORD, ItemQuality.COMMON,
            NameTranslation.ITEM_WEAPON_SWORD_COMMON, NameTranslation.DESC_ITEM_SWORD_COMMON, FileLinkTemplate.FLYING_SWORD,
            new HashSet<>() {{
                add(StoreType.BLACKSMITH);
            }}),
    SWORD_UNCOMMON(
            15.0, 150.0, 25, false, ItemSubType.SWORD, ItemQuality.UNCOMMON,
            NameTranslation.ITEM_WEAPON_SWORD_UNCOMMON, NameTranslation.DESC_ITEM_SWORD_UNCOMMON, FileLinkTemplate.FLYING_SWORD,
            new HashSet<>() {{
                add(StoreType.BLACKSMITH);
            }}),
    STICK(
            1.0, 0, 1, true, ItemSubType.CRAFTING, ItemQuality.COMMON,
            NameTranslation.ITEM_MATERIAL_STICK, NameTranslation.DESC_ITEM_STICK, null,
            null),
    STONE(
            3.0, 0, 1, true, ItemSubType.CRAFTING, ItemQuality.COMMON,
            NameTranslation.ITEM_MATERIAL_STONE, NameTranslation.DESC_ITEM_STONE, null,
            new HashSet<>() {{
                add(StoreType.BLACKSMITH);
                add(StoreType.MERCHANT);
            }}),
    IMPOSSIBLE_ITEM(
            666.0, 0, 666, false, ItemSubType.CRAFTING, ItemQuality.LEGENDARY,
            NameTranslation.ITEM_MATERIAL_STONE, NameTranslation.DESC_ITEM_STONE, null,
            new HashSet<>() {{
                add(StoreType.EMPTY);
            }});

    private final double value;
    private final double durability;
    private final long price;
    private final boolean isStackable;
    private final ItemSubType subType;
    private final ItemQuality quality;
    private final NameTranslation nameTranslation;
    private final NameTranslation description;
    @Getter(AccessLevel.NONE)
    private final FileLinkTemplate imageLink;
    private final Set<StoreType> storeType;

    ItemTemplate(double value, double durability, long price, boolean isStackable, ItemSubType subType, ItemQuality quality, NameTranslation translation, NameTranslation description, FileLinkTemplate imageLink, Set<StoreType> storeType) {
        this.value = value;
        this.durability = durability;
        this.price = price;
        this.isStackable = isStackable;
        this.subType = subType;
        this.quality = quality;
        nameTranslation = translation;
        this.description = description;
        this.imageLink = imageLink;
        this.storeType = storeType;
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
