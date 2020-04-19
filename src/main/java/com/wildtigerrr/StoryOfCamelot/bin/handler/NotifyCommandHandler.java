package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.impl.AsyncMessageSender;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

@Service
public class NotifyCommandHandler extends CommandHandler {

    private final AsyncMessageSender asyncMessageSender;

    protected NotifyCommandHandler(ResponseManager messages, TranslationManager translation, AsyncMessageSender asyncMessageSender) {
        super(messages, translation);
        this.asyncMessageSender = asyncMessageSender;
    }

    @Override
    void process(IncomingMessage message) {
        TextIncomingMessage textMessage = (TextIncomingMessage) message;
        int value;
        try {
            value = textMessage.getParsedCommand().intByNum(1);
        } catch (InvalidInputException e) {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(translation.getMessage("commands.notify_error", textMessage))
                    .targetId(message)
                    .build()
            );
            return;
        }
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("commands.notify", textMessage, new Object[]{value}))
                .targetId(message)
                .build()
        );

        try {
            asyncMessageSender.sendDelayedMessage(
                    value * 1000,
                    "commands.notify_finish",
                    message.getUserId()
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
