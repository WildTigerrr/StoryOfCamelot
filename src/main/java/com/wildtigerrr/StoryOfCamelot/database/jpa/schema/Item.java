package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.ItemsTemplate;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemQuality;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemSubType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "item")
@Getter @Setter
public class Item extends SimpleObject {

    @Id
    @SequenceGenerator(name = "item_seq", sequenceName = "item_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    @GenericGenerator(
            name = "item_seq",
            strategy = "com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = IdGenerator.VALUE_PREFIX_PARAMETER, value = "a0i0")
            })
    @Setter(AccessLevel.NONE)
    private String id;
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

    @Override
    public ObjectType type() {
        return ObjectType.ITEM;
    }

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

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", value=" + value +
                ", durability=" + durability +
                ", price=" + price +
                ", type=" + type +
                '}';
    }
}
