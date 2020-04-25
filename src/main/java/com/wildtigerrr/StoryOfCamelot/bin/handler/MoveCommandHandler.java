package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.GameMovement;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import org.springframework.stereotype.Service;

@Service
public class MoveCommandHandler extends TextMessageHandler {

    private final GameMovement gameMovement;

    public MoveCommandHandler(ResponseManager messages, TranslationManager translation, GameMovement gameMovement) {
        super(messages, translation);
        this.gameMovement = gameMovement;
    }

    @Override
    public void process(IncomingMessage message) {
        gameMovement.handleMove(message);
    }

}
