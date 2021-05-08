package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.TestFactory;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdate;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdateMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class TextMessageHandlerTest extends ServiceBaseTest {

    @Autowired
    private TextMessageHandler textMessageHandler;

    @Autowired
    private TestFactory testFactory;

    @MockBean
    private StartCommandHandler startCommandHandler;

    @Test
    void whenMoveAlreadyScheduledShouldInformPlayerTest() {
        // Given
        Player player = testFactory.createPlayer();

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/rattata").build()).build().get()
        );
        message.setPlayer(player);

        // When
        textMessageHandler.process(message);

        // Then
        verify(startCommandHandler).process(any());
    }

}