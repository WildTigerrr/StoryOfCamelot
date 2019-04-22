package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.database.schema.enums.ItemStatus;

import javax.persistence.*;

@Entity
@Table(name = "backpack")
public class Backpack {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getCurrentDurability() {
        return currentDurability;
    }

    public void setCurrentDurability(Integer currentDurability) {
        this.currentDurability = currentDurability;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}