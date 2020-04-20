package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.DiceIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.DiceResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

@Service
public class DiceCommandHandler extends CommandHandler {

    protected DiceCommandHandler(ResponseManager messages, TranslationManager translation) {
        super(messages, translation);
    }

    @Override
    public void process(IncomingMessage message) {
        DiceIncomingMessage diceMessage = (DiceIncomingMessage) message;
        if (diceMessage.getResponse() == null) {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(translation.getMessage("commands.dice-output", diceMessage, new Object[]{diceMessage.getValue()}))
                    .targetId(diceMessage)
                    .applyMarkup(true).build()
            );
            messages.sendMessage(DiceResponseMessage.builder()
                    .incomingMessage(diceMessage)
                    .targetId(diceMessage).build()
            );
        } else {
            String text = "У меня *" + diceMessage.getResponse() + "*. ";
            if (diceMessage.getValue() > diceMessage.getResponse()) {
                messages.sendMessage(TextResponseMessage.builder()
                        .text(text + "Ты победил!")
                        .targetId(diceMessage)
                        .applyMarkup(true).build()
                );
            } else if (diceMessage.getValue() < diceMessage.getResponse()) {
                messages.sendMessage(TextResponseMessage.builder()
                        .text(text + "Я победил!")
                        .targetId(diceMessage)
                        .applyMarkup(true).build()
                );
            } else {
                messages.sendMessage(TextResponseMessage.builder()
                        .text(text + "Ничья!")
                        .targetId(diceMessage)
                        .applyMarkup(true).build()
                );
            }
        }
    }

}
