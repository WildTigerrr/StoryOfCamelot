package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.base.BattleLog;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
//@ActiveProfiles("local")
public class BattleHandlerTest extends ServiceBaseTest {

    @Autowired
    private BattleHandler battleHandler;

    @Test
    void whenOverkillShouldWinTest() {
        Player strongFighter = new Player("test", "Strong One", new Location(LocationTemplate.TRADING_SQUARE));
        strongFighter.stats().setHealth(100);
        strongFighter.setCurrentHealth(100.0);
        strongFighter.setLanguage(Language.ENG);
        strongFighter.stats().setStrength(100);
        Player weakFighter = new Player("test", "Weak One", new Location(LocationTemplate.TRADING_SQUARE));
        weakFighter.setCurrentHealth(1.0);
        weakFighter.setLanguage(Language.ENG);

        BattleLog battleLog = battleHandler.fight(strongFighter, weakFighter, Language.RUS);

        assertTrue(weakFighter.getCurrentHealth() < 0);

        assertTrue(battleLog.getLog().get(0).contains("Strong One"));
        assertTrue(battleLog.getLog().get(0).contains("Weak One"));
        assertTrue(battleLog.getLog().get(battleLog.getLog().size() - 1).contains("Strong One"));
        assertTrue(battleLog.isWin());

        battleLog.getLog().forEach(System.out::println);
    }

}
