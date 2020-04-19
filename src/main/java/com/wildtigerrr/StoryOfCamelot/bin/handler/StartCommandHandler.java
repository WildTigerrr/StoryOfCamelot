package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

@Service
public class StartCommandHandler extends CommandHandler {

    public StartCommandHandler(ResponseManager messages, TranslationManager translation) {
        super(messages, translation);
    }

    @Override
    protected void process(IncomingMessage message) {
        messages.sendMessage(TextResponseMessage.builder()
                .text("Hello world!")
                .targetId(message)
                .build()
        );
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messages.sendMessage(TextResponseMessage.builder()
                .text("Hello world! 2")
                .targetId(message)
                .build()
        );
    }
}
