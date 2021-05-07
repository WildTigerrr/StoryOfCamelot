package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.bin.service.Time;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.LocationDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.LocationNearDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationNear;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdate;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdateMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.verify;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MoveCommandHandlerTest extends ServiceBaseTest {

    @Autowired
    private MoveCommandHandler moveCommandHandler;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CacheProvider cacheService;

    @Autowired
    private LocationNearDao locationNearDao;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private TranslationManager translation;

    @MockBean
    private ResponseManager messages;

    @Captor
    ArgumentCaptor<ResponseMessage> messageArguments;

    @Test
    void whenMoveShouldMovePlayerTest() throws InterruptedException {
        // Given
        String playerId = "testMovePlayerId";
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        Location target = new Location(LocationTemplate.FOREST);
        LocationNear distance = new LocationNear(initial, target, 4);
        locationNearDao.save(distance);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.MOVE.name() + " " + target.getId()).build()).build().get()
        );

        Player player = new Player(playerId, "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        cacheService.add(CacheType.PLAYER_STATE, new PlayerState(player.getId()));
        message.setPlayer(player);

        // When
        TimeDependentActions.clearActions();
        moveCommandHandler.process(message);

        // Then
        assertNotEquals(LocationTemplate.FOREST.getTranslations().getName(player), playerService.getPlayer(playerId).getLocation().getName(player));

        Thread.sleep(Time.seconds(2));

        assertNotEquals(LocationTemplate.FOREST.getTranslations().getName(player), playerService.getPlayer(playerId).getLocation().getName(player));

        Thread.sleep(Time.seconds(4));

        assertEquals(LocationTemplate.FOREST.getTranslations().getName(player), playerService.getPlayer(playerId).getLocation().getName(player));
    }

    @Test
    void whenMoveAlreadyScheduledShouldInformPlayerTest() throws InterruptedException {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        Location target = new Location(LocationTemplate.FOREST);
        LocationNear distance = new LocationNear(initial, target, 4);
        locationNearDao.save(distance);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.MOVE.name() + " " + target.getId()).build()).build().get()
        );

        Player player = new Player("testId", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        cacheService.add(CacheType.PLAYER_STATE, new PlayerState(player.getId()));
        message.setPlayer(player);

        // When
        moveCommandHandler.process(message);
        Thread.sleep(Time.seconds(1));
        moveCommandHandler.process(message);

        // Then
        assertNotEquals(LocationTemplate.FOREST.getTranslations().getName(player), playerService.getPlayer("testId").getLocation().getName(player));

        verify(messages, Mockito.atMost(3)).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("movement.location.in-progress", player), messageArguments.getValue().getText());
    }

    @Test
    void whenMoveAlreadyScheduledShouldInformPlayerForMoveTest() throws InterruptedException {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        Location target = new Location(LocationTemplate.FOREST);
        LocationNear distance = new LocationNear(initial, target, 4);
        locationNearDao.save(distance);

        TextIncomingMessage messageMove = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.MOVE.name() + " " + target.getId()).build()).build().get()
        );
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text("/" + Command.MOVE.name()).build()).build().get()
        );

        Player player = new Player("testId", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        cacheService.add(CacheType.PLAYER_STATE, new PlayerState(player.getId()));
        messageMove.setPlayer(player);
        message.setPlayer(player);

        // When
        moveCommandHandler.process(messageMove);
        Thread.sleep(Time.seconds(1));
        moveCommandHandler.process(message);

        // Then
        assertNotEquals(LocationTemplate.FOREST.getTranslations().getName(player), playerService.getPlayer("testId").getLocation().getName(player));

        verify(messages, Mockito.atMost(3)).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("movement.location.in-progress", player), messageArguments.getValue().getText());
    }

    @Test
    void whenMoveAlreadyScheduledShouldInformPlayerEvenIfNotInStateTest() throws InterruptedException {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        Location target = new Location(LocationTemplate.FOREST);
        LocationNear distance = new LocationNear(initial, target, 4);
        locationNearDao.save(distance);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.MOVE.name() + " " + target.getId()).build()).build().get()
        );

        Player player = new Player("testId", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        cacheService.add(CacheType.PLAYER_STATE, new PlayerState(player.getId()));
        message.setPlayer(player);

        // When
        moveCommandHandler.process(message);
        Thread.sleep(Time.seconds(1));
        cacheService.add(CacheType.PLAYER_STATE, new PlayerState(player.getId()));
        moveCommandHandler.process(message);

        // Then
        assertNotEquals(LocationTemplate.FOREST.getTranslations().getName(player), playerService.getPlayer("testId").getLocation().getName(player));

        verify(messages, Mockito.atMost(3)).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("movement.location.in-progress", player), messageArguments.getValue().getText());
    }

    @Test
    void whenDoesNotHaveDistanceShouldInformPlayerTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        Location target = new Location(LocationTemplate.FOREST);
        locationDao.saveAll(new ArrayList<>(){{
            add(initial);
            add(target);
        }});

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.MOVE.name() + " " + target.getId()).build()).build().get()
        );

        Player player = new Player("testId", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        cacheService.add(CacheType.PLAYER_STATE, new PlayerState(player.getId()));
        message.setPlayer(player);

        // When
        moveCommandHandler.process(message);

        // Then
        assertNotEquals(LocationTemplate.FOREST.getTranslations().getName(player), playerService.getPlayer("testId").getLocation().getName(player));

        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("movement.location.no-connection", player), messageArguments.getValue().getText());
    }

    @Test
    void whenMoveShouldSendAvailableOptionsTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        Location target = new Location(LocationTemplate.FOREST);
        LocationNear distance = new LocationNear(initial, target, 4);
        locationNearDao.save(distance);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text("/" + Command.MOVE.name()).build()).build().get()
        );

        Player player = new Player("testId", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        cacheService.add(CacheType.PLAYER_STATE, new PlayerState(player.getId()));
        message.setPlayer(player);

        // When
        moveCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("movement.location.select", player), messageArguments.getValue().getText());
    }

    @Test
    void whenMoveMayBeBlockedTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        locationDao.save(initial);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text("/" + Command.MOVE.name()).build()).build().get()
        );

        Player player = new Player("testId", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        cacheService.add(CacheType.PLAYER_STATE, new PlayerState(player.getId()));
        message.setPlayer(player);

        // When
        moveCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("movement.location.blocked", player), messageArguments.getValue().getText());
    }

    @Test
    void whenMoveWithWrongIdShouldInformPlayerTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        Location target = new Location(LocationTemplate.FOREST);
        LocationNear distance = new LocationNear(initial, target, 4);
        locationNearDao.save(distance);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.MOVE.name()).build()).build().get()
        );

        Player player = new Player("testId", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        cacheService.add(CacheType.PLAYER_STATE, new PlayerState(player.getId()));
        message.setPlayer(player);

        // When
        moveCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("movement.location.unknown-location", player), messageArguments.getValue().getText());
    }

}