package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.database.schema.enums.ItemStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "backpack")
@Getter @Setter
public class Backpack {

    // TODO Backpack - junction, BackpackItem as item

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Integer id;
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

}