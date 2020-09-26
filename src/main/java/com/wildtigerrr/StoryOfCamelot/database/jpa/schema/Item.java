package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.ItemTemplate;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemQuality;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemSubType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;

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
    @Enumerated(EnumType.STRING)
    private NameTranslation nameTranslation;
    private String systemName;
    @Enumerated(EnumType.STRING)
    private NameTranslation description;

    private Double value; // Defence, Damage, Speed etc
    private Double durability;
    private Double price;
    private Boolean isStackable;

    @Enumerated(EnumType.STRING)
    private ItemSubType type;
    @Enumerated(EnumType.STRING)
    private ItemQuality quality;
    @ManyToOne
    @JoinColumn(name = "filelink_id")
    private FileLink imageLink;

    @Override
    public ObjectType type() {
        return ObjectType.ITEM;
    }

    protected Item() {
    }

    public Item(ItemTemplate template) {
        this(
                template.getValue(),
                template.getDurability(),
                template.getPrice(),
                template.isStackable(),
                template.getSubType(),
                template.getQuality(),
                template.getNameTranslation(),
                template.name(),
                template.getDescription(),
                template.getFileLink()
        );
    }

    public Item(Double value, Double durability, Double price, Boolean isStackable, ItemSubType type, ItemQuality quality,
                NameTranslation translation, String systemName, NameTranslation description
    ) {
        this(value, durability, price, isStackable, type, quality, translation, systemName, description, null);
    }

    public Item(Double value, Double durability, Double price, Boolean isStackable, ItemSubType type, ItemQuality quality,
                NameTranslation translation, String systemName, NameTranslation description, FileLink imageLink
    ) {
        this.value = value;
        this.durability = durability;
        this.price = price;
        this.isStackable = isStackable;
        this.type = type;
        this.quality = quality;
        this.systemName = systemName;
        this.nameTranslation = translation;
        this.imageLink = imageLink;
    }

    public String getName(Language lang) {
        return nameTranslation.getName(lang);
    }

    public String getName(@NotNull Player player) {
        return getName(player.getLanguage());
    }

    public String getDescribe(Language lang) {
        return description.getName(lang);
    }

    public String getDescribe(@NotNull Player player) {
        return getDescribe(player.getLanguage());
    }
    
    public boolean isEquippable() {
        return getType() == ItemSubType.SWORD;
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
