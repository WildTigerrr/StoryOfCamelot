package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.BackpackType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "backpack")
@Getter @Setter
@Log4j2
public class Backpack extends SimpleObject {

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
    private BackpackType type;
    @OneToMany(
            cascade = {CascadeType.ALL},
            mappedBy = "backpack"
    )
    private List<BackpackItem> items = new ArrayList<>();

    protected Backpack() {
    }

    public Backpack(Player player) {
        this.player = player;
        this.type = BackpackType.MAIN;
    }

    public Backpack(Player player, BackpackType type) {
        this.player = player;
        this.type = type;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BACKPACK;
    }

    public void put(@NotNull Item item, @Nullable ItemStatus status) {
        put(new BackpackItem(this, item, status));
    }

    public void put(@NotNull Item item) {
        put(new BackpackItem(this, item, null));
    }

    public void put(@NotNull BackpackItem item) {
        if (addQuantity(item)) return;
        item.setBackpack(this);
        addBackpackItem(item);
    }

    public void put(@NotNull List<BackpackItem> items) {
        items.forEach(this::put);
    }

    public boolean remove(@NotNull Item item, int quantity) {
        // TODO Add removal with multiple similar items
        BackpackItem backpackItem = getItemById(item.getId());
        return backpackItem != null && backpackItem.remove(quantity);
    }

    private void addBackpackItem(BackpackItem item) {
        items.add(item);
    }

    public void removeBackpackItem(BackpackItem item) {
        items.remove(item);
    }

    public BackpackItem getItemById(String itemId) {
        if (itemId == null) return null;
        else {

            log.debug(this::getItems);
            Optional<BackpackItem> backpackItemOptional = getItems().stream()
                    .filter(item -> item.getItem().getId().equals(itemId))
                    .map(Optional::ofNullable).findFirst().orElse(Optional.empty());
            log.debug(backpackItemOptional);
            return backpackItemOptional.orElse(null);
        }
    }

    public BackpackItem getItemByItemId(String itemId) {
        if (itemId == null) return null;
        else {

            log.debug(this::getItems);
            Optional<BackpackItem> backpackItemOptional = getItems().stream()
                    .filter(item -> item.getId() != null && item.getId().equals(itemId))
                    .map(Optional::ofNullable).findFirst().orElse(Optional.empty());
            log.debug(backpackItemOptional);
            return backpackItemOptional.orElse(null);
        }
    }

    private boolean addQuantity(BackpackItem item) {
        if (item.getItem().getIsStackable()) {
            log.debug("Adding quantity for: " + item.getItem().getName(Language.ENG));
            BackpackItem existing = getItemById(item.getItem().getId());
            log.debug(existing);
            if (existing != null) {
                return existing.add(item.getQuantity());
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Backpack{" +
                "id='" + id + '\'' +
                ", items=" + items +
                '}';
    }
}