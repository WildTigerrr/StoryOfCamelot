package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;

public abstract class CommandHandler {

    protected final ResponseManager messages;
    protected final TranslationManager translation;

    protected CommandHandler(ResponseManager messages, TranslationManager translation) {
        this.messages = messages;
        this.translation = translation;
    }

    abstract void process(IncomingMessage message);

}
