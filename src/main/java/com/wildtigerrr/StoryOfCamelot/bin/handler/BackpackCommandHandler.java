package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.BackpackService;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
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
        TextIncomingMessage textIncomingMessage = (TextIncomingMessage) message;
        if (!textIncomingMessage.getParsedCommand().hasExtraParams()) {
            sendBackpack(message);
        } else {
            System.out.println(textIncomingMessage.getParsedCommand().paramByNum(0));
            System.out.println(textIncomingMessage.getParsedCommand().paramByNum(1));
            // /backpack 1 item_equip a0bi00000000007
            // /backpack 1 item_info a0bi00000000007
            // /backpack page 0
        }
    }

    private void sendBackpack(IncomingMessage message) {
        sendBackpack(message.getPlayer(), 1);
    }

    private void sendBackpack(Player player, int page) {
        Backpack backpack = backpackService.findMainByPlayerId(player.getId());
        StringBuilder builder = new StringBuilder();
        builder.append(translation.getMessage("player.backpack.info", player));
        backpack.getItems().forEach(item -> builder.append(item.backpackInfo(translation)));
        messages.sendMessage(TextResponseMessage.builder().by(player)
                .keyboard(KeyboardManager.getKeyboardForBackpack(backpack, page, translation))
                .text(builder.toString()).build()
        );
    }

}
