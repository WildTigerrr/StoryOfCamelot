package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.DropTemplate;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.ItemTemplate;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.MobTemplate;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Item;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.MobDrop;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class DropCalculatorTest extends ServiceBaseTest {

    @Autowired
    private DropCalculator calculator;

    @Test
    void whenTest() {
        calculator.calculate(getTestMobDrops()).forEach(log::info);
    }

    private List<MobDrop> getTestMobDrops() {
        HashMap<String, Mob> mobs = getTestMobs();
        HashMap<String, Item> items = getTestItems();
        List<MobDrop> drops = new ArrayList<>();
        for (DropTemplate template : DropTemplate.values()) {
            drops.add(new MobDrop(mobs.get(template.getMobName()), items.get(template.getItemName()))
                    .setQuantityRandom(template.getQuantityRandom(), template.getQuantityMin(), template.getQuantityLimit())
                    .setDurabilityRandom(template.getDurabilityRandom(), template.getDurabilityMin(), template.getDurabilityMax()));
        }
        return drops;
    }

    private HashMap<String, Mob> getTestMobs() {
        return MobTemplate.getMobs();
    }

    private HashMap<String, Item> getTestItems() {
        return ItemTemplate.getItems();
    }

}