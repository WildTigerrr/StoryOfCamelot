package com.wildtigerrr.StoryOfCamelot.testutils;

import lombok.Builder;
import lombok.Data;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.Chat;

@Builder
@Data
public class TestUpdateChat {

    @Builder.Default
    private Long id = 1L;

    public Chat get() {
        Chat chat = new Chat();
        ReflectionTestUtils.setField(chat, "id", id);
        return chat;
    }

}
