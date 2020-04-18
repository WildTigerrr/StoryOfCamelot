package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
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

class TelegramWebHookHandlerMockTest extends ServiceBaseTest {

    private final Update update = new Update();
    private final Message message = new Message();
    private final User user = new User();
    private final Chat chat = new Chat();

    @Autowired
    private TelegramWebHookHandler telegramWebHookHandler;

    @MockBean
    private ResponseHandler responseHandlerMock;

    @Captor
    ArgumentCaptor<Update> messageArguments;

    @Test
    void whenNewMessageShouldPassToHandlerTest() {
        ReflectionTestUtils.setField(user, "id", 1);
        ReflectionTestUtils.setField(message, "from", user);
        ReflectionTestUtils.setField(chat, "id", 2L);
        ReflectionTestUtils.setField(message, "chat", chat);
        ReflectionTestUtils.setField(message, "messageId", 3);
        ReflectionTestUtils.setField(message, "text", "Success");
        ReflectionTestUtils.setField(update, "message", message);

        telegramWebHookHandler.onWebhookUpdateReceived(update);

        verify(responseHandlerMock).handleUpdate(messageArguments.capture());

        assertEquals("Success", messageArguments.getValue().getMessage().getText());
    }

    @Test
    void whenGettingParamsShouldReturnConfig() {
        assertEquals(BotConfig.WEBHOOK_TOKEN, telegramWebHookHandler.getBotToken());
        assertEquals(BotConfig.WEBHOOK_USER, telegramWebHookHandler.getBotUsername());
        assertEquals(BotConfig.WEBHOOK_ADMIN, telegramWebHookHandler.getBotPath());
    }

}
