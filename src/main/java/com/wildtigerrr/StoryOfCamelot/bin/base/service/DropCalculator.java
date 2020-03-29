package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.RandomDistribution;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.BackpackItem;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.MobDrop;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DropCalculator {

    public List<BackpackItem> calculate(List<MobDrop> dropMap) {
        List<BackpackItem> items = new ArrayList<>();
        int quantity;
        double value;
        for (MobDrop drop : dropMap) {
            quantity = (int) of(drop.getQuantityRandom(), drop.getQuantityMin(), drop.getQuantityLimit()).drop();
            if (quantity <= 0) continue;
            if (drop.getItem().getDurability() != null) {
                value = of(drop.getDurabilityRandom(), drop.getDurabilityMin(), drop.getDurabilityMax()).drop();
                items.add(new BackpackItem(drop.getItem(), null).setCurrentDurability(value));
            } else {
                items.add(new BackpackItem(drop.getItem(), null).setQuantity(quantity));
            }
        }
        return items;
    }

    private DropValue of(RandomDistribution distribution, double valueMin, double valueMax) {
        return new DropValue(distribution, valueMin, valueMax);
    }

    static class DropValue {
        private RandomDistribution distributionType;
        private double valueMin;
        private double valueMax;

        public double drop() {
            if (distributionType == null) {
                return valueMin;
            } else {
                return valueMin + ((double) distributionType.nextInt((int) (valueMax * 10)) / 10.0) - 1;
            }
        }

        public DropValue(RandomDistribution distributionType, double valueMin, double valueMax) {
            this.distributionType = distributionType;
            this.valueMin = valueMin;
            this.valueMax = valueMax;
        }
    }

}
