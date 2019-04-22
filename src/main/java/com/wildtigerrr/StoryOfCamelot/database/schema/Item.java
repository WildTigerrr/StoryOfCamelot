package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.database.schema.enums.ItemQuality;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.ItemType;
import software.amazon.ion.Decimal;

import javax.persistence.*;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Decimal value; // Defence, Damage, Speed etc
    private Integer durability;
    private Decimal price;
    private Boolean isStackable;

    @Enumerated(EnumType.STRING)
    private ItemType type;
    @Enumerated(EnumType.STRING)
    private ItemQuality quality;
    @ManyToOne(optional = true)
    @JoinColumn(name = "filelink_id")
    private FileLink imageLink;

    protected Item() {
    }

    public Item(Decimal value, Integer durability, Decimal price, ItemType type, ItemQuality quality) {
        this.value = value;
        this.durability = durability;
        this.price = price;
        this.type = type;
        this.quality = quality;
    }

    public Item(Decimal value, Integer durability, Decimal price, ItemType type, ItemQuality quality, FileLink imageLink) {
        this.value = value;
        this.durability = durability;
        this.price = price;
        this.type = type;
        this.quality = quality;
        this.imageLink = imageLink;
    }

    public Integer getId() {
        return id;
    }

    public Decimal getValue() {
        return value;
    }

    public void setValue(Decimal value) {
        this.value = value;
    }

    public Integer getDurability() {
        return durability;
    }

    public void setDurability(Integer durability) {
        this.durability = durability;
    }

    public Decimal getPrice() {
        return price;
    }

    public void setPrice(Decimal price) {
        this.price = price;
    }

    public Boolean getStackable() {
        return isStackable;
    }

    public void setStackable(Boolean stackable) {
        isStackable = stackable;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public ItemQuality getQuality() {
        return quality;
    }

    public void setQuality(ItemQuality quality) {
        this.quality = quality;
    }

    public FileLink getImageLink() {
        return imageLink;
    }

    public void setImageLink(FileLink imageLink) {
        this.imageLink = imageLink;
    }
}
