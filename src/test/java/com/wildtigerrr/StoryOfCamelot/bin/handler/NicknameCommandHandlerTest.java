package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.LocationDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NicknameCommandHandlerTest extends ServiceBaseTest {

    @Autowired
    private NicknameCommandHandler nicknameCommandHandler;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private TranslationManager translation;

    @Autowired
    private CacheProvider cacheService;

    @MockBean
    private ResponseManager messages;

    @Captor
    ArgumentCaptor<ResponseMessage> messageArguments;

    @Test
    void whenEmptyNicknameShouldInformPlayerTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        locationDao.save(initial);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " ").build()).build().get()
        );

        Player player = new Player("testIdEmpty", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        message.setPlayer(player);

        // When
        nicknameCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("player.nickname.empty", player), messageArguments.getValue().getText());
    }

    @Test
    void whenNicknameContainsSpecialCharactersShouldInformPlayerTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        locationDao.save(initial);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " /\\").build()).build().get()
        );

        Player player = new Player("testIdSpecial", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        message.setPlayer(player);

        // When
        nicknameCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("player.nickname.wrong-symbols", player), messageArguments.getValue().getText());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void whenNicknameDuplicationShouldInformPlayerTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        locationDao.save(initial);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " Nickname").build()).build().get()
        );

        Player player = new Player("testIdDuplicate", "Nickname", initial);
        Player player2 = new Player("test2Id", "Nickname2", initial);
        player2.setLanguage(Language.ENG);
        playerService.createIfNotExist(player);
        player2 = playerService.createIfNotExist(player2);
        message.setPlayer(player2);

        // When
        nicknameCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("player.nickname.duplicate", player2, new Object[]{player.getNickname()}), messageArguments.getValue().getText());
    }

    @Test
    void whenNicknameTooLongShouldInformPlayerTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        locationDao.save(initial);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.").build()).build().get()
        );

        Player player = new Player("testIdLong", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        message.setPlayer(player);

        // When
        nicknameCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("player.nickname.too-long", player, new Object[]{nicknameCommandHandler.getNicknameMaxLength()}), messageArguments.getValue().getText());
    }

    @Test
    void whenNicknameMultipleSpacesShouldInformUserTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        locationDao.save(initial);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " Lorem    ipsum  dolor").build()).build().get()
        );

        Player player = new Player("testIdSpaces", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        message.setPlayer(player);

        // When
        nicknameCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("player.nickname.wrong-symbols", player), messageArguments.getValue().getText());
    }

    @Test
    void whenNicknameFineShouldSendActionsTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        locationDao.save(initial);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " Lorem ipsum dolor").build()).build().get()
        );

        Player player = new Player("testIdFine", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        cacheService.add(CacheType.PLAYER_STATE, new PlayerState(player.getId()));
        message.setPlayer(player);

        // When
        nicknameCommandHandler.process(message);

        // Then
        verify(messages, times(2)).sendMessage(messageArguments.capture());

        List<ResponseMessage> messages = messageArguments.getAllValues();
        assertEquals(translation.getMessage("player.nickname.accept", player, new Object[]{"Lorem ipsum dolor"}), messages.get(0).getText());
    }

}