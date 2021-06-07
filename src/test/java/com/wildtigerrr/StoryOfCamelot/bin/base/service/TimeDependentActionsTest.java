package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ActionType;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.bin.service.ScheduledAction;
import com.wildtigerrr.StoryOfCamelot.bin.service.Time;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.LocationDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TimeDependentActionsTest extends ServiceBaseTest {

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TranslationManager translation;

    @Test
    void whenAddActionShouldStartCheckingTest() throws InterruptedException {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        locationDao.save(initial);

        Player player = new Player("testId", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player.stats().raiseStat(Stats.HEALTH, 20, Language.ENG, translation);
        player.setCurrentHealth(5.0);
        player = playerService.createIfNotExist(player);

        TimeDependentActions.clearActions();
        assertTrue(TimeDependentActions.getPlayerToScheduled().isEmpty());

        // When
        TimeDependentActions.scheduleAction(new ScheduledAction(
                Time.seconds(1), ActionType.REGENERATION, player.getId(), "10"
        ), false);

        // Then
        assertEquals(Double.valueOf(5.0), playerService.findById(player.getId()).getCurrentHealth());

        Thread.sleep(Time.seconds(6));

        assertEquals(Double.valueOf(15.0), playerService.findById(player.getId()).getCurrentHealth());
        assertFalse(TimeDependentActions.getPlayerToScheduled().isEmpty());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void whenAddActionShouldStartCheckingAndContinueRegenTest() throws InterruptedException {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        locationDao.save(initial);

        Player player = new Player("testId", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player.stats().raiseStat(Stats.HEALTH, 14, Language.ENG, translation);
        player.setCurrentHealth(5.0);
        player = playerService.createIfNotExist(player);

        TimeDependentActions.clearActions();
        assertTrue(TimeDependentActions.getPlayerToScheduled().isEmpty());

        // When
        TimeDependentActions.scheduleAction(new ScheduledAction(
                Time.seconds(1), ActionType.REGENERATION, player.getId(), "10"
        ), false);

        // Then
        assertEquals(Double.valueOf(5.0), playerService.findById(player.getId()).getCurrentHealth());

        Thread.sleep(Time.seconds(6));

        assertEquals(Double.valueOf(15.0), playerService.findById(player.getId()).getCurrentHealth());
        assertTrue(TimeDependentActions.getPlayerToScheduled().isEmpty());
    }

    @Test
    void whenAlreadyHaveOneOfATypeActionShouldNotAllowOneMoreTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        locationDao.save(initial);

        Player player = new Player("testId", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player.stats().raiseStat(Stats.HEALTH, 14, Language.ENG, translation);
        player.setCurrentHealth(5.0);
        player = playerService.createIfNotExist(player);

        TimeDependentActions.clearActions();
        assertTrue(TimeDependentActions.getPlayerToScheduled().isEmpty());

        // When
        TimeDependentActions.scheduleAction(new ScheduledAction(
                Time.minutes(1), ActionType.MOVEMENT, player.getId(), initial.getId()
        ), true);
        TimeDependentActions.scheduleAction(new ScheduledAction(
                Time.minutes(2), ActionType.MOVEMENT, player.getId(), initial.getId()
        ), true);

        // Then
        assertEquals(1, TimeDependentActions.getPlayerToScheduled().size());
    }

}