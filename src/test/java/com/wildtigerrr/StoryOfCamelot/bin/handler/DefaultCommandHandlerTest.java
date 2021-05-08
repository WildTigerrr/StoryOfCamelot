package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.TestFactory;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class DefaultCommandHandlerTest extends ServiceBaseTest {

    @Autowired
    private DefaultCommandHandler defaultCommandHandler;

    @Autowired
    private TestFactory testFactory;

    @Autowired
    private TranslationManager translation;

    @MockBean
    private ResponseManager messages;

    @Captor
    ArgumentCaptor<ResponseMessage> messageArguments;

    @Test
    void whenMoveAlreadyScheduledShouldInformPlayerTest() {
        // Given
        Player player = testFactory.createPlayer();
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/rattata").build()).build().get()
        );
        message.setPlayer(player);

        // When
        defaultCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("commands.unknown", player), messageArguments.getValue().getText());
    }

}