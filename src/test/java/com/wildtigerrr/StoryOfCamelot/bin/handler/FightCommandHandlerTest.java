package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.TestFactory;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Skill;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationPossible;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.EnemyState;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdate;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdateMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Log4j2
class FightCommandHandlerTest extends ServiceBaseTest {

    @Autowired
    FightCommandHandler fightCommandHandler;

    @Autowired
    private TestFactory testFactory;

    @Autowired
    private CacheProvider cacheService;

    @Autowired
    private TranslationManager translation;

    @Autowired
    private PlayerService playerService;

    @MockBean
    private ResponseManager messages;

    @Captor
    ArgumentCaptor<ResponseMessage> messageArguments;

    @Test
    void whenRandomizingEnemyShouldIncludeAllVariantsTest() {
        List<LocationPossible> possibleList = new ArrayList<>();
        Location testLocation = TestFactory.createLocationMock();
        possibleList.add(new LocationPossible(testFactory.createMob("First"), testLocation, 2));
        possibleList.add(new LocationPossible(testFactory.createMob("Second"), testLocation, 1));

        int firstCounter = 0;
        int secondCounter = 0;
        for (int i = 0; i < 100; i++) {
            if (((Mob) ReflectionTestUtils.invokeMethod(fightCommandHandler, "getRandomMob", possibleList)).getSystemName().equals("First")) {
                firstCounter++;
            } else {
                secondCounter++;
            }
        }
        log.debug("First: " + firstCounter + ", second: " + secondCounter);
        assertTrue(secondCounter > 0);
        assertTrue(firstCounter > secondCounter);
    }

    @Test
    void whenInitialFightShouldReceiveListOfAvailableFightingActionsTest() {
        // Given
        Player player = testFactory.createPlayer(true);
        player.getSkills().add(Skill.STRONG_ATTACK);
        PlayerState playerState = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, player.getId());
        Mob enemy = testFactory.createMob("First");
        cacheService.add(CacheType.PLAYER_STATE, playerState.setEnemy(enemy));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text(ReplyButton.FIGHT.getLabel(player)).build()).build().get()
        );
        message.setPlayer(player);

        // When
        fightCommandHandler.process(message);

        // Then
        verify(messages, atMost(2)).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("battle.start", message), messageArguments.getAllValues().get(0).getText());
        assertEquals(translation.getMessage("battle.choose-next-action", message), messageArguments.getAllValues().get(1).getText());
        assertTrue(((TextResponseMessage) messageArguments.getAllValues().get(1)).getKeyboard().toString().contains(Skill.STRONG_ATTACK.getLabel(player.getLanguage())));

        playerState = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, player.getId());
        assertEquals(enemy.getId(), playerState.getEnemy().getId());
        assertEquals(enemy.getHitpointsMax(), playerState.getEnemyState().getHitpoints());
    }

    @Test
    void whenFightingActionShouldCalculateHitpointsTest() {
        // Given
        Player player = testFactory.createPlayer(true);
        player.stats().setStrength(5);
        player.stats().setHealth(50);
        player.setCurrentHealth(50.0);
        player.getSkills().add(Skill.STRONG_ATTACK);
        playerService.update(player);
        PlayerState playerState = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, player.getId());
        Mob enemy = testFactory.createMob("First");
        enemy.setHitpointsMax(50);
        playerState.setEnemyState(EnemyState.of(enemy));
        playerState.setEnemy(enemy).initBattleLog("Battle against First");
        cacheService.add(CacheType.PLAYER_STATE, playerState);
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text(ReplyButton.FIGHT_STRONG_ATTACK.getLabel(player)).build()).build().get()
        );
        message.setPlayer(player);
        assertSame(enemy.getHitpointsMax(), playerState.getEnemyState().getHitpoints());
        assertEquals((int) player.stats().getHealth(), player.getCurrentHealth().intValue());

        // When
        fightCommandHandler.process(message);

        // Then
        verify(messages, atLeastOnce()).sendMessage(messageArguments.capture());

        playerState = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, player.getId());
        assertEquals(enemy.getId(), playerState.getEnemy().getId());
        assertTrue(enemy.getHitpointsMax() > playerState.getEnemyState().getHitpoints());
        assertTrue(playerState.getLastBattle().getBattleHistory().contains("Battle against First"));

        player = playerService.getPlayer(player.getExternalId());
        assertTrue(player.stats().getHealth() > player.getCurrentHealth());
    }

}