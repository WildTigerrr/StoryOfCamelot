package com.wildtigerrr.StoryOfCamelot.testutils;

import lombok.Builder;
import lombok.Data;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Builder
@Data
public class TestUpdateCallbackQuery {

    @Builder.Default
    private String id = "1";

    public CallbackQuery get(TestUpdateMessage message) {
        CallbackQuery callbackQuery = new CallbackQuery();
        ReflectionTestUtils.setField(callbackQuery, "id", id);
        ReflectionTestUtils.setField(callbackQuery, "data", message.getText());
        ReflectionTestUtils.setField(callbackQuery, "message", message.get());
        return callbackQuery;
    }

}
