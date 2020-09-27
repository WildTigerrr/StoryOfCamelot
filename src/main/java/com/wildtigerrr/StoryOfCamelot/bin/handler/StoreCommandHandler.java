package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.ActionHandler;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class StoreCommandHandler extends TextMessageHandler {

    private final ActionHandler actionHandler;

    public StoreCommandHandler(ResponseManager messages, TranslationManager translation, ActionHandler actionHandler) {
        super(messages, translation);
        this.actionHandler = actionHandler;
    }

    @Override
    public void process(IncomingMessage message) {
        switch (message.getCommand()) {
            case STORES: sendAvailableStores(message); break;
            case STORE_SELECT: sendStore((TextIncomingMessage) message); break;
        }
    }

    private void sendStore(TextIncomingMessage message) {
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text("Ассоритимент для магазина: " + message.text())
                .build()
        );
    }

    private void sendAvailableStores(IncomingMessage message) {
        if (message.getPlayer().getLocation().getHasStores()) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .keyboard(KeyboardManager.getReplyByStores(message.getPlayer().getLocation().getStores(), message.getPlayer().getLanguage()))
                    .text("Магазины здесь:")
                    .build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder()
                    .text("Здесь нет магазинов")
                    .build()
            );
            actionHandler.sendAvailableActions(message.getPlayer());
        }
    }

}
