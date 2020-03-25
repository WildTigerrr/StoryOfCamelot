package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "backpack_item")
@Getter @Setter
public class BackpackItem extends SimpleObject {

    @Id
    @SequenceGenerator(name = "backpack_item_seq", sequenceName = "backpack_item_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "backpack_item_seq")
    @GenericGenerator(
            name = "backpack_item_seq",
            strategy = "com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = IdGenerator.VALUE_PREFIX_PARAMETER, value = "a0bi")
            })
    @Setter(AccessLevel.NONE)
    private String id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "backpack_id")
    private Backpack backpack;
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer currentDurability;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private ItemStatus status;
    private Boolean isDeleted;

    protected BackpackItem() {
    }

    public BackpackItem(Backpack backpack, Item item, ItemStatus status) {
        this.backpack = backpack;
        this.item = item;
        currentDurability = item.getDurability();
        this.status = status;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BACKPACK_ITEM;
    }

    public String backpackInfo(TranslationManager translation) {
        return translation.getMessage("player.backpack.item", backpack.getPlayer(), new Object[]{getItem().getName(backpack.getPlayer())});
    }

    @Override
    public String toString() {
        return "BackpackItem{" +
                "id='" + id + '\'' +
                ", item=" + item +
                ", currentDurability=" + currentDurability +
                ", quantity=" + quantity +
                ", status=" + status +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
