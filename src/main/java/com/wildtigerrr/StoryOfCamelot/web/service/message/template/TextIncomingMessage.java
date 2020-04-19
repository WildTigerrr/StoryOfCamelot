package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TextIncomingMessage extends IncomingMessage {

    public TextIncomingMessage(Update update) {
        super(update);
    }

}
