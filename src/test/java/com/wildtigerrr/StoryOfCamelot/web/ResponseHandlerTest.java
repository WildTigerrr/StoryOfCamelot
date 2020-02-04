package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;

public class ResponseHandlerTest extends ServiceBaseTest {

    @Autowired
    private ResponseHandler responseHandler;

    @MockBean
    private GameMain gameMainMock;

    @Captor
    ArgumentCaptor<UpdateWrapper> messageArguments;

    private Update update = new Update();
    private Message message = new Message();
    private User user = new User();
    private Chat chat = new Chat();

    @Test
    void whenPlainMessageShouldCreateNonQueryWrapperTest() {
        ReflectionTestUtils.setField(user, "id", 1);
        ReflectionTestUtils.setField(message, "from", user);
        ReflectionTestUtils.setField(chat, "id", 2L);
        ReflectionTestUtils.setField(message, "chat", chat);
        ReflectionTestUtils.setField(message, "messageId", 3);
        ReflectionTestUtils.setField(message, "text", "Success");
        ReflectionTestUtils.setField(update, "message", message);

        responseHandler.handleUpdate(update);

        verify(gameMainMock).handleTextMessage(messageArguments.capture());

        assertEquals("Success", messageArguments.getValue().getText());
        assertFalse(messageArguments.getValue().isQuery());
    }

}
