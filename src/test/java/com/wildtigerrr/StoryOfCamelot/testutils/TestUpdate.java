package com.wildtigerrr.StoryOfCamelot.testutils;

import lombok.Builder;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

@Builder
public class TestUpdate {

    @Builder.Default
    private boolean isCallback = false;
    @Builder.Default
    private TestUpdateCallbackQuery callback = TestUpdateCallbackQuery.builder().build();
    @Builder.Default
    private TestUpdateMessage message = TestUpdateMessage.builder().build();

    public Update get() {
        Update update = new Update();
        if (isCallback) {
            ReflectionTestUtils.setField(update, "callbackQuery", callback.get(message));
        } else {
            ReflectionTestUtils.setField(update, "message", message.get());
        }
        return update;
    }
}
