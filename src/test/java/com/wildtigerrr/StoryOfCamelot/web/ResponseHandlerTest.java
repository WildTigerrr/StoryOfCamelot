package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdate;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdateMessage;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

public class ResponseHandlerTest extends ServiceBaseTest {

    @Autowired
    private ResponseHandler responseHandler;

    @MockBean
    private GameMain gameMainMock;

    @Captor
    ArgumentCaptor<UpdateWrapper> messageArguments;

    @Test
    void whenPlainMessageShouldCreateNonQueryWrapperTest() {
        Update update = TestUpdate.builder().message(TestUpdateMessage.builder().text("Success").build()).build().get();

        responseHandler.handleUpdate(update);

        verify(gameMainMock).handleTextMessage(messageArguments.capture());

        assertEquals("Success", messageArguments.getValue().getText());
        assertFalse(messageArguments.getValue().isQuery());
    }

    @Test
    void whenCallbackQueryShouldCreateQueryWrapperTest() {
        Update update = TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("Success").build()).build().get();

        responseHandler.handleUpdate(update);

        verify(gameMainMock).handleTextMessage(messageArguments.capture());

        assertEquals("Success", messageArguments.getValue().getText());
        assertTrue(messageArguments.getValue().isQuery());
    }

}
