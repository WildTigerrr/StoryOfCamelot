package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class TextMessageHandler extends CommandHandler {

    public TextMessageHandler(ResponseManager messages, TranslationManager translation) {
        super(messages, translation);
    }

    @Override
    @Transactional
    public void process(IncomingMessage message) {
        message.getCommand().handler().process(message);
        message.logFinish();
    }

}