package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.BackpackType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ItemStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "backpack")
@Getter @Setter
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
        addBackpackItem(new BackpackItem(this, item, status));
    }

    public void put(@NotNull Item item) {
        addBackpackItem(new BackpackItem(this, item, null));
    }

    public void put(@NotNull BackpackItem item) {
        item.setBackpack(this);
        addBackpackItem(item);
    }

    public void put(@NotNull List<BackpackItem> items) {
        items.forEach(this::put);
    }

    public void addBackpackItem(BackpackItem item) {
        items.add(item);
    }

    public void removeBackpackItem(BackpackItem item) {
        items.remove(item);
    }

    @Override
    public String toString() {
        return "Backpack{" +
                "id='" + id + '\'' +
                ", items=" + items +
                '}';
    }
}