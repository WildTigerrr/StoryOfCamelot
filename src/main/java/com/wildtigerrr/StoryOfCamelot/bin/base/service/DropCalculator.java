package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.RandomDistribution;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.BackpackItem;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.MobDrop;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class DropCalculator {

    public List<BackpackItem> calculate(List<MobDrop> dropMap) {
        List<BackpackItem> items = new ArrayList<>();
        int quantity;
        double value;
        for (MobDrop drop : dropMap) {
            quantity = (int) of(drop.getQuantityRandom(), drop.getQuantityMin(), drop.getQuantityLimit()).drop();
            if (quantity <= 0) continue;
            if (!drop.getItem().getIsStackable()) {
                value = of(drop.getDurabilityRandom(), drop.getDurabilityMin(), drop.getDurabilityMax()).drop();
                items.add(new BackpackItem(drop.getItem()).setCurrentDurability(value));
            } else {
                items.add(new BackpackItem(drop.getItem()).setQuantity(quantity));
            }
        }
        return items;
    }

    private DropValue of(RandomDistribution distribution, double valueMin, double valueMax) {
        return new DropValue(distribution, valueMin, valueMax);
    }

    static class DropValue {
        private final RandomDistribution distributionType;
        private final double valueMin;
        private final double valueMax;

        public double drop() {
            if (distributionType == null) {
                return valueMin;
            } else {
                return valueMin + distributionType.nextInt((int) (valueMax - valueMin + 1)) - 1;
            }
        }

        public DropValue(RandomDistribution distributionType, double valueMin, double valueMax) {
            this.distributionType = distributionType;
            this.valueMin = valueMin;
            this.valueMax = valueMax;
        }
    }

}
