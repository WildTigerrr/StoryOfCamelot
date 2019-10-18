package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
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

class WebHookHandlerMockTest extends ServiceBaseTest {

    private Update update = new Update();
    private Message message = new Message();
    private User user = new User();
    private Chat chat = new Chat();

    @Autowired
    private WebHookHandler webHookHandler;

    @MockBean
    private ResponseHandler responseHandlerMock;

//    @Captor
//    ArgumentCaptor<UpdateWrapper> messageArguments;

    @Test
    void whenPlainMessageShouldCreateNonQueryWrapperTest() {
        ReflectionTestUtils.setField(user, "id", 1);
        ReflectionTestUtils.setField(message, "from", user);
        ReflectionTestUtils.setField(chat, "id", 2L);
        ReflectionTestUtils.setField(message, "chat", chat);
        ReflectionTestUtils.setField(message, "messageId", 3);
        ReflectionTestUtils.setField(message, "text", "Success");
        ReflectionTestUtils.setField(update, "message", message);

        webHookHandler.onWebhookUpdateReceived(update);

//        verify(responseHandlerMock).handleMessage(messageArguments.capture());
        verify(responseHandlerMock).handleUpdate(update);

//        assertEquals("Success", messageArguments.getValue().getText());
//        assertFalse(messageArguments.getValue().isQuery());
    }

}
