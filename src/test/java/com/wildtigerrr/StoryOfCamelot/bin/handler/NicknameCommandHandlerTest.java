package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.TestFactory;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdate;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdateMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NicknameCommandHandlerTest extends ServiceBaseTest {

    @Autowired
    private NicknameCommandHandler nicknameCommandHandler;

    @Autowired
    private TestFactory testFactory;

    @Autowired
    private TranslationManager translation;

    @MockBean
    private ResponseManager messages;

    @Captor
    ArgumentCaptor<ResponseMessage> messageArguments;

    @Test
    void whenEmptyNicknameShouldInformPlayerTest() {
        // Given
        Player player = testFactory.createPlayer();
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " ").build()).build().get()
        );
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
        Player player = testFactory.createPlayer();
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " /\\").build()).build().get()
        );
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
        Player player = testFactory.createPlayer();
        Player player2 = testFactory.createPlayer();
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " " + player.getNickname()).build()).build().get()
        );
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
        Player player = testFactory.createPlayer();
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.").build()).build().get()
        );
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
        Player player = testFactory.createPlayer();
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " Lorem    ipsum  dolor").build()).build().get()
        );
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
        Player player = testFactory.createPlayer(true);
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.NICKNAME.name().toLowerCase() + " Lorem ipsum dolor").build()).build().get()
        );
        message.setPlayer(player);

        // When
        nicknameCommandHandler.process(message);

        // Then
        verify(messages, times(2)).sendMessage(messageArguments.capture());

        List<ResponseMessage> messages = messageArguments.getAllValues();
        assertEquals(translation.getMessage("player.nickname.accept", player, new Object[]{"Lorem ipsum dolor"}), messages.get(0).getText());
    }

}