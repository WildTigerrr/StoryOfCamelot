package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "backpack")
@Getter @Setter
public class Backpack extends SimpleObject {

    // TODO Backpack - junction, BackpackItem as item

    @Id
    @SequenceGenerator(name = "backpack_seq", sequenceName = "backpack_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "backpack_seq")
    @GenericGenerator(
            name = "backpack_seq",
            strategy = "com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = IdGenerator.VALUE_PREFIX_PARAMETER, value = "a0b0")
            })
    @Setter(AccessLevel.NONE)
    private String id;
    @ManyToOne
    private Player player;
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer currentDurability;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private ItemStatus status;
    private Boolean isDeleted;

    protected Backpack() {
    }

    public Backpack(Player player, Item item, ItemStatus status) {
        this.player = player;
        this.item = item;
        currentDurability = item.getDurability();
        this.status = status;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BACKPACK;
    }
}