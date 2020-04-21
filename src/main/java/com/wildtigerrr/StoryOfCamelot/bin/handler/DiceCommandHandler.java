package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.DiceIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.DiceResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class DiceCommandHandler extends CommandHandler {

    protected DiceCommandHandler(ResponseManager messages, TranslationManager translation) {
        super(messages, translation);
    }

    @Override
    public void process(IncomingMessage message) {
        DiceIncomingMessage diceMessage = (DiceIncomingMessage) message;
        if (diceMessage.hasAnswer()) {
            sendDiceResult(diceMessage);
            sendDice(diceMessage);
        } else {
            sendMinigameResult(diceMessage);
        }
        diceMessage.logFinish();
    }

    private void sendDiceResult(DiceIncomingMessage message) {
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("commands.dice-output", message, new Object[]{message.getValue()}))
                .targetId(message)
                .applyMarkup(true).build()
        );
    }

    private void sendDice(DiceIncomingMessage message) {
        messages.sendMessage(DiceResponseMessage.builder()
                .incomingMessage(message)
                .targetId(message).build()
        );
    }

    private void sendMinigameResult(DiceIncomingMessage message) {
        String botResult = "У меня *" + message.getResponse() + "*. ";
        if (message.getValue() > message.getResponse()) {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(botResult + "Ты победил!")
                    .targetId(message)
                    .applyMarkup(true).build()
            );
        } else if (message.getValue() < message.getResponse()) {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(botResult + "Я победил!")
                    .targetId(message)
                    .applyMarkup(true).build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(botResult + "Ничья!")
                    .targetId(message)
                    .applyMarkup(true).build()
            );
        }
    }

}
