package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.service.NumberUtils;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private Double maximumDurability;
    private Double currentDurability;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private ItemStatus status;
    private Boolean isDeleted;

    protected BackpackItem() {
    }

    public BackpackItem(@Nullable Backpack backpack, @NotNull Item item, @Nullable ItemStatus status) {
        this.backpack = backpack;
        this.item = item;
        this.currentDurability = item.getDurability();
        this.maximumDurability = item.getDurability();
        this.status = status;
        this.quantity = 1;
    }

    public BackpackItem(@NotNull Item item, @Nullable ItemStatus status) {
        this(null, item, status);
    }

    public BackpackItem(@NotNull Item item) {
        this(null, item, null);
    }

    public BackpackItem setCurrentDurability(Double currentDurability) {
        this.currentDurability = currentDurability;
        return this;
    }

    public BackpackItem setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public boolean add(Integer quantity) {
        // TODO Add maximum stackable quantity
        if (item.getIsStackable()) {
            this.quantity += quantity;
            return true;
        }
        return false;
    }

    public boolean remove(Integer quantity) {
        if (item.getIsStackable()) {
            if (this.getQuantity().equals(quantity)) {
                getBackpack().removeBackpackItem(this);
                return true;
            } else if (this.getQuantity() > quantity){
                this.quantity -= quantity;
                return true;
            }
        } else {
            getBackpack().removeBackpackItem(this);
        }
        return false;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BACKPACK_ITEM;
    }

    public boolean isEquipped() {
        return getStatus() == ItemStatus.EQUIPPED;
    }

    public String backpackInfo(TranslationManager translation) {
        String template = getMaximumDurability() > 0 ? "player.backpack.item-durability" : "player.backpack.item-quantity";
        double value = getMaximumDurability() > 0 ? getDurabilityPercent() : getQuantity();
        return translation.getMessage(template, backpack.getPlayer(), new Object[]{
                getItem().getName(backpack.getPlayer()), value
        });
    }

    public String backpackInfo(TranslationManager translation, Language lang) {
        String template = getMaximumDurability() > 0 ? "player.backpack.item-durability" : "player.backpack.item-quantity";
        double value = getMaximumDurability() > 0 ? getDurabilityPercent() : getQuantity();
        return translation.getMessage(template, lang, new Object[]{
                getItem().getName(lang), value
        });
    }

    private double getDurabilityPercent() {
        return NumberUtils.round(getCurrentDurability() / getMaximumDurability() * 100);
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
