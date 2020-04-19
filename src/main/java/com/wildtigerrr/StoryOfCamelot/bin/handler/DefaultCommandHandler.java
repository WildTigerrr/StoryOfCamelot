package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

@Service
public class DefaultCommandHandler extends CommandHandler {

    public DefaultCommandHandler(ResponseManager messages, TranslationManager translation) {
        super(messages, translation);
    }

    @Override
    void process(IncomingMessage message) {
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("commands.unknown", message))
                .targetId(message)
                .build()
        );
    }

}
