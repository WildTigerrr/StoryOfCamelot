package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

@Service
public class StartCommandHandler extends TextMessageHandler {

    public StartCommandHandler(ResponseManager messages, TranslationManager translation) {
        super(messages, translation);
    }

    @Override
    public void process(IncomingMessage message) {
        messages.sendMessage(TextResponseMessage.builder().lang(message)
                .text("Hello world!")
                .targetId(message)
                .build()
        );
    }

}
