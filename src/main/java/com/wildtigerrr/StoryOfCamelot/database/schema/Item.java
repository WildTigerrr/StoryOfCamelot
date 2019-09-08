package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.ItemsTemplate;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.ItemQuality;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.ItemSubType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "item")
@Getter @Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Integer id;
    private Double value; // Defence, Damage, Speed etc
    private Integer durability;
    private Double price;
    private Boolean isStackable;

    @Enumerated(EnumType.STRING)
    private ItemSubType type;
    @Enumerated(EnumType.STRING)
    private ItemQuality quality;
    @ManyToOne(optional = true)
    @JoinColumn(name = "filelink_id")
    private FileLink imageLink;

    protected Item() {
    }

    public Item(ItemsTemplate template) {
        this(
                template.getValue(),
                template.getDurability(),
                template.getPrice(),
                template.getSubType(),
                template.getQuality(),
                template.getFileLink()
        );
    }

    public Item(Double value, Integer durability, Double price, ItemSubType type, ItemQuality quality) {
        this(value, durability, price, type, quality, null);
    }

    public Item(Double value, Integer durability, Double price, ItemSubType type, ItemQuality quality, FileLink imageLink) {
        this.value = value;
        this.durability = durability;
        this.price = price;
        this.type = type;
        this.quality = quality;
        this.imageLink = imageLink;
    }

}
