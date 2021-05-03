package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;

import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkillTest extends ServiceBaseTest {

    @Test
    void whenSkillCalculationShouldReturnValidSkillStrength() {
        Player strongFighter = new Player("test", "Strong One", new Location(LocationTemplate.TRADING_SQUARE));
        strongFighter.stats().setStrength(100);
        strongFighter.stats().setAgility(150);

        System.out.println(strongFighter.stats().getStrength());

        assertEquals(100.0, Skill.BASIC_ATTACK.calculateStrength(strongFighter));
        assertEquals(200.0, Skill.FAST_ATTACK.calculateStrength(strongFighter));
    }

}