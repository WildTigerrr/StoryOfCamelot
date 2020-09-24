package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.ActionHandler;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PlayerCommandHandler extends TextMessageHandler {

    private final ActionHandler actionHandler;

    public PlayerCommandHandler(ResponseManager messages, TranslationManager translation, ActionHandler actionHandler) {
        super(messages, translation);
        this.actionHandler = actionHandler;
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
                .keyboard(KeyboardManager.getReplyByButtons(actionHandler.getAvailableActions(message.getPlayer()), message.getPlayer().getLanguage()))
                .text(message.getPlayer().toString())
                .applyMarkup(true).build()
        );
    }

}
