package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Builder
@Getter
public class TextResponseMessage implements ResponseMessage {

    @Builder.Default
    private final ResponseType type = ResponseType.TEXT;
    @NonNull
    private final String targetId;
    private final String text;
    @Builder.Default
    private final boolean applyMarkup = false;
    private final ReplyKeyboard keyboard;

}
