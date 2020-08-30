package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.ParsedCommand;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.impl.AsyncMessageSender;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

@Service
public class NotifyCommandHandler extends TextMessageHandler {

    private final AsyncMessageSender asyncMessageSender;

    protected NotifyCommandHandler(ResponseManager messages, TranslationManager translation, AsyncMessageSender asyncMessageSender) {
        super(messages, translation);
        this.asyncMessageSender = asyncMessageSender;
    }

    @Override
    public void process(IncomingMessage message) {
        TextIncomingMessage textMessage = (TextIncomingMessage) message;
        int value;
        try {
            value = textMessage.getParsedCommand().intByNum(1);
        } catch (InvalidInputException e) {
            messages.sendMessage(TextResponseMessage.builder().lang(message)
                    .text(translation.getMessage("commands.notify_error", textMessage))
                    .targetId(message)
                    .build()
            );
            return;
        }
        messages.sendMessage(TextResponseMessage.builder().lang(message)
                .text(translation.getMessage("commands.notify", textMessage, new Object[]{value}))
                .targetId(message)
                .build()
        );

        String text = getText(textMessage, value);
        try {
            asyncMessageSender.sendDelayedMessage(
                    value * 60 * 1000,
                    translation.getMessage("commands.notify_finish", textMessage, new Object[]{value}) + text,
                    message.getUserId(),
                    message.getPlayer().getLanguage()
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getText(TextIncomingMessage textMessage, int value) {
        if (textMessage.getParsedCommand().paramsCount() <= 2) return "";
        return ": " + textMessage.text().split(String.valueOf(value), 2)[1];
    }

}
