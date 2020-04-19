package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.web.bot.update.ParsedCommand;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class TextIncomingMessage extends IncomingMessage {

    private final ParsedCommand parsedCommand;

    public TextIncomingMessage(Update update) {
        super(update);
        this.parsedCommand = new ParsedCommand(getCommand(), text());
    }

}
