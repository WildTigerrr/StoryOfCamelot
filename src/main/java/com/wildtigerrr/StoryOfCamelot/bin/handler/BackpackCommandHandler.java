package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.BackpackItem;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.BackpackService;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.ParsedCommand;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class BackpackCommandHandler extends TextMessageHandler {

    private final BackpackService backpackService;

    public BackpackCommandHandler(ResponseManager messages, TranslationManager translation, BackpackService backpackService) {
        super(messages, translation);
        this.backpackService = backpackService;
    }

    @Override
    public void process(IncomingMessage message) {
        TextIncomingMessage textIncomingMessage = (TextIncomingMessage) message;
        ParsedCommand command = textIncomingMessage.getParsedCommand();
        if (!command.hasExtraParams()) {
            sendBackpack(message);
        } else if (command.paramsCount() > 2) {
            switch (command.paramByNum(2)) {
                case "page": sendBackpack(textIncomingMessage.getPlayer(), command.intByNum(1)); break;
                case "item": {
                    switch (command.paramByNum(3)) {
                        case "info": sendItemInfo((TextIncomingMessage) message); break;
                        case "equip": equipItem((TextIncomingMessage) message); break;
                        case "unequip": unequipItem((TextIncomingMessage) message); break;
                        default: log.debug(command.paramByNum(3));
                    }
                }
                default: log.debug(command.paramByNum(2));
            }
        } else {
            log.debug(command.paramsCount());
            log.debug(command.paramByNum(2));
        }
    }

    private void equipItem(TextIncomingMessage message) {
        ParsedCommand command = message.getParsedCommand();
        if (command.paramsCount() < 4) return;
        // equip id command.paramByNum(4);
    }

    private void unequipItem(TextIncomingMessage message) {
        ParsedCommand command = message.getParsedCommand();
        if (command.paramsCount() < 4) return;
        // unequip id command.paramByNum(4);
    }

    private void sendItemInfo(TextIncomingMessage message) {
        ParsedCommand command = message.getParsedCommand();
        if (command.paramsCount() < 4) return;
        Backpack backpack = backpackService.findMainByPlayerId(message.getPlayer().getId());
        BackpackItem item = backpack.getItemById(command.paramByNum(4));
        if (item != null) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(item.getItem().getDescribe(message.getPlayer()))
                    .applyMarkup(true).build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("У Вас нет этого предмета").build()
            );
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