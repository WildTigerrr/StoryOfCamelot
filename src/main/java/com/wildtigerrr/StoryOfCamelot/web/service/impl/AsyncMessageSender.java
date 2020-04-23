package com.wildtigerrr.StoryOfCamelot.web.service.impl;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncMessageSender {

    protected final ResponseManager messages;

    public AsyncMessageSender(ResponseManager messages) {
        this.messages = messages;
    }

    @Async
    public void sendDelayedMessage(int delay, String message, String targetId, Language language) throws InterruptedException {
        Thread.sleep(delay);
        messages.sendMessage(TextResponseMessage.builder().lang(language)
                .text(message)
                .targetId(targetId)
                .build()
        );
    }

}
