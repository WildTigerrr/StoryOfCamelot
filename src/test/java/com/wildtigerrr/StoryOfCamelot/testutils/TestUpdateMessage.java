package com.wildtigerrr.StoryOfCamelot.testutils;

import lombok.Builder;
import lombok.Data;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.Message;

@Builder
@Data
public class TestUpdateMessage {

    @Builder.Default
    private TestUpdateUser user = TestUpdateUser.builder().build();
    @Builder.Default
    private Integer messageId = 1;
    @Builder.Default
    private String text = "Test";
    @Builder.Default
    private TestUpdateChat chat = TestUpdateChat.builder().build();

    public Message get() {
        Message message = new Message();
        ReflectionTestUtils.setField(message, "chat", chat.get());
        ReflectionTestUtils.setField(message, "from", user.get());
        ReflectionTestUtils.setField(message, "messageId", messageId);
        ReflectionTestUtils.setField(message, "text", text);
        return message;
    }

}
