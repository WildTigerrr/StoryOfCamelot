package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.database.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BattleHandlerTest extends ServiceBaseTest {

    @Autowired
    private BattleHandler battleHandler;

    @Test
    void whenOverkillShouldWinTest() {
        Player strongFighter = new Player("test", "Strong One", new Location(LocationTemplate.TRADING_SQUARE));
        strongFighter.setHealth(100);
        strongFighter.setStrength(100);
        Fighter weakFighter = new Player("test", "Weak One", new Location(LocationTemplate.TRADING_SQUARE));

        List<String> battleLog = battleHandler.fight(strongFighter, weakFighter, Language.RUS);

        assertTrue(battleLog.get(0).contains("Strong One"));
        assertTrue(battleLog.get(0).contains("Weak One"));
        assertTrue(battleLog.get(battleLog.size() - 1).contains("Strong One"));

        battleLog.forEach(System.out::println);
    }

    @Test
    void whenTest() {
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            System.out.println(random.nextInt(100));
        }
    }

}
