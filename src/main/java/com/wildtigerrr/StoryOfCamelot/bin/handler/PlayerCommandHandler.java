package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PlayerCommandHandler extends TextMessageHandler {

    public PlayerCommandHandler(ResponseManager messages, TranslationManager translation) {
        super(messages, translation);
    }

    @Override
    public void process(IncomingMessage message) {
        switch (message.getCommand()) {
            case ME: sendPlayerInfo(message); break;
        }
    }

    private void sendPlayerInfo(IncomingMessage message) {
        log.warn(message.getPlayer().toString());
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(message.getPlayer().toString())
                .applyMarkup(true).build()
        );
    }

}
