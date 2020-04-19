package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StartCommandHandler extends CommandHandler {

    private final AsyncMessageSender asyncMessageSender;

    public StartCommandHandler(ResponseManager messages, TranslationManager translation, AsyncMessageSender asyncMessageSender) {
        super(messages, translation);
        this.asyncMessageSender = asyncMessageSender;
    }

    @Override
    public void process(IncomingMessage message) {
        messages.sendMessage(TextResponseMessage.builder()
                .text("Hello world!")
                .targetId(message)
                .build()
        );
        try {
            asyncMessageSender.sendDelayedMessage(10000, "Hello world! 2", message.getUserId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
