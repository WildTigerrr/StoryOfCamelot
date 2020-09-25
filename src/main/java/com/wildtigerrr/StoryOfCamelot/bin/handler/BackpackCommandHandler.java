package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.BackpackService;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

@Service
public class BackpackCommandHandler extends TextMessageHandler {

    private final BackpackService backpackService;

    public BackpackCommandHandler(ResponseManager messages, TranslationManager translation, BackpackService backpackService) {
        super(messages, translation);
        this.backpackService = backpackService;
    }

    @Override
    public void process(IncomingMessage message) {
        switch (message.getCommand()) {
            case BACKPACK: sendBackpack(message); break;
        }
    }

    private void sendBackpack(IncomingMessage message) {
        Backpack backpack = backpackService.findMainByPlayerId(message.getPlayer().getId());
        StringBuilder builder = new StringBuilder();
        builder.append(translation.getMessage("player.backpack.info", message));
        backpack.getItems().forEach(item -> builder.append(item.backpackInfo(translation)));
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .keyboard(KeyboardManager.getKeyboardForBackpack(backpack, 1, translation))
                .text(builder.toString()).build()
        );
    }
}
