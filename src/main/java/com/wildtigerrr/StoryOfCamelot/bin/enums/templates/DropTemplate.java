package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import com.wildtigerrr.StoryOfCamelot.bin.enums.RandomDistribution;
import lombok.Getter;

@Getter
public enum DropTemplate {

    FLYING_SWORD_SWORD(MobTemplate.FLYING_SWORD, ItemTemplate.SWORD_COMMON,
            RandomDistribution.EXPONENTIAL_OFTEN, 0, 1,
            RandomDistribution.GAUSSIAN, 50, 100),
    FLYING_SWORD_STICK(MobTemplate.FLYING_SWORD, ItemTemplate.STICK,
            RandomDistribution.EXPONENTIAL_SUPER_OFTEN, 0, 20),
    FLYING_SWORD_STONE(MobTemplate.FLYING_SWORD, ItemTemplate.STONE,
            RandomDistribution.EXPONENTIAL_OFTEN, 0, 4),


    SUPER_FLYING_SWORD_SWORD(MobTemplate.SUPER_FLYING_SWORD, ItemTemplate.SWORD_UNCOMMON,
            RandomDistribution.EXPONENTIAL_OFTEN, 0, 1,
            RandomDistribution.GAUSSIAN, 20, 120);

    private final MobTemplate mob;
    private final ItemTemplate item;
    private final RandomDistribution quantityRandom;
    private final int quantityMin;
    private final int quantityLimit;
    private final RandomDistribution durabilityRandom;
    private final double durabilityMin;
    private final double durabilityMax;

    DropTemplate(MobTemplate mob, ItemTemplate item, RandomDistribution quantityRandom, int quantityMin, int quantityLimit, RandomDistribution durabilityRandom, double durabilityMin, double durabilityMax) {
        this.mob = mob;
        this.item = item;
        this.quantityRandom = quantityRandom;
        this.quantityMin = quantityMin;
        this.quantityLimit = quantityLimit;
        this.durabilityRandom = durabilityRandom;
        this.durabilityMin = durabilityMin;
        this.durabilityMax = durabilityMax;
    }

    DropTemplate(MobTemplate mob, ItemTemplate item, RandomDistribution quantityRandom, int quantityMin, int quantityLimit) {
        this(mob, item, quantityRandom, quantityMin, quantityLimit, null, 0, 0);
    }

    public String getMobName() {
        return mob.name();
    }

    public String getItemName() {
        return item.name();
    }

}
