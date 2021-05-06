package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.handler.DefaultCommandHandler;
import com.wildtigerrr.StoryOfCamelot.bin.handler.DiceCommandHandler;
import com.wildtigerrr.StoryOfCamelot.bin.handler.ImageMessageHandler;
import com.wildtigerrr.StoryOfCamelot.bin.handler.TextMessageHandler;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ResponseHandler {

    private final TextMessageHandler textMessageHandler;
    private final DiceCommandHandler diceCommandHandler;
    private final ImageMessageHandler imageMessageHandler;
    private final DefaultCommandHandler defaultCommandHandler;


    public ResponseHandler(
            TextMessageHandler textMessageHandler,
            DiceCommandHandler diceCommandHandler,
            ImageMessageHandler imageMessageHandler,
            DefaultCommandHandler defaultCommandHandler
    ) {
        this.diceCommandHandler = diceCommandHandler;
        this.textMessageHandler = textMessageHandler;
        this.imageMessageHandler = imageMessageHandler;
        this.defaultCommandHandler = defaultCommandHandler;
    }

    public void proceed(IncomingMessage message) {
        switch (message.getMessageType()) {
            case MESSAGE:
            case CALLBACK:
                textMessageHandler.process(message);
                break;
            case PHOTO:
                imageMessageHandler.process(message);
                break;
            case DICE:
                diceCommandHandler.process(message);
                break;
            default:
                defaultCommandHandler.process(message);
                log.info(message.senderLog() + ": Unknown message " + message.getMessageType() + " - Finished in " + message.elapsedTime() + "ms");
        }
    }

}