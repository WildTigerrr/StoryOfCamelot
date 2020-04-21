package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class DiceIncomingMessage extends IncomingMessage {

    private final int value;
    @Setter
    private Integer response;

    public DiceIncomingMessage(Update update) {
        super(update);
        value = update.getMessage().getDice().getValue();
    }

    public boolean hasAnswer() {
        return this.response != null;
    }

}
