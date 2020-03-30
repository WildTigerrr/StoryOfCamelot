package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.bin.enums.RandomDistribution;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "mob_drop")
@Getter
public class MobDrop extends SimpleObject {

    @Id
    @SequenceGenerator(name = "mob_drop_seq", sequenceName = "mob_drop_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mob_drop_seq")
    @GenericGenerator(
            name = "mob_drop_seq",
            strategy = "com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = IdGenerator.VALUE_PREFIX_PARAMETER, value = "a0md")
            })
    @Setter(AccessLevel.NONE)
    private String id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "mob_id")
    private Mob mob;
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private Item item;
    @Enumerated(EnumType.STRING)
    private RandomDistribution quantityRandom;
    private int quantityMin;
    private int quantityLimit;

    private RandomDistribution durabilityRandom;
    private double durabilityMin;
    private double durabilityMax;

    @Override
    public ObjectType type() {
        return ObjectType.MOB_DROP;
    }

    protected MobDrop() {
    }

    public MobDrop(@NotNull Mob mob, @NotNull Item item) {
        this.mob = mob;
        this.item = item;
    }

    public MobDrop setQuantityRandom(RandomDistribution distribution, int min, int max) {
        quantityRandom = distribution;
        quantityMin = min;
        quantityLimit = max;
        return this;
    }

    public MobDrop setDurabilityRandom(RandomDistribution distribution, double min, double max) {
        durabilityRandom = distribution;
        durabilityMin = min;
        durabilityMax = max;
        return this;
    }

}