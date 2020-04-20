package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.DiceIncomingMessage;
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
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("commands.dice-output", diceMessage, new Object[]{diceMessage.getValue()}))
                .targetId(diceMessage)
                .applyMarkup(true).build()
        );
    }

}
