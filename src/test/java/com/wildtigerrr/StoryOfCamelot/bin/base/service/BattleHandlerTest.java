package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.MobTemplate;
import com.wildtigerrr.StoryOfCamelot.database.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BattleHandlerTest extends ServiceBaseTest {

    @Autowired
    private BattleHandler battleHandler;

    @Test
    void whenOverkillShouldWinTest() {
        Player strongFighter = new Player("test", "Strong One", new Location(LocationTemplate.TRADING_SQUARE));
        strongFighter.stats().setHealth(100);
        strongFighter.stats().setStrength(100);
        Fighter weakFighter = new Player("test", "Weak One", new Location(LocationTemplate.TRADING_SQUARE));

        BattleLog battleLog = battleHandler.fight(strongFighter, weakFighter, Language.RUS);

        assertTrue(battleLog.getLog().get(0).contains("Strong One"));
        assertTrue(battleLog.getLog().get(0).contains("Weak One"));
        assertTrue(battleLog.getLog().get(battleLog.getLog().size() - 1).contains("Strong One"));
        assertTrue(battleLog.isWin());

        battleLog.getLog().forEach(System.out::println);
    }

    @Test
    void whenTest() {
        Fighter fighter = new Mob(MobTemplate.FLYING_SWORD);
        System.out.println(fighter.getClass());

        Random random = new Random();
        List<Integer> numbers = new ArrayList<>();
        int value;
        for (int i = 0; i < 200; i++) {
            value = random.nextInt(100);
            numbers.add(value + 1);
            System.out.println(value > 89);
        }
        System.out.println(Arrays.toString(numbers.stream().sorted().toArray()));
    }

}
