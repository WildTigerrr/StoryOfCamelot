package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncMessageSender {

    protected final ResponseManager messages;
    protected final TranslationManager translation;

    public AsyncMessageSender(ResponseManager messages, TranslationManager translation) {
        this.messages = messages;
        this.translation = translation;
    }

    @Async
    public void sendDelayedMessage(int delay, String message) throws InterruptedException {
        Thread.sleep(delay);
        messages.sendMessage(TextResponseMessage.builder()
                .text("Hello world!")
                .targetId(message)
                .build()
        );
    }

}
