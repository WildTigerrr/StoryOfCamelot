package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class StickerIncomingMessage extends IncomingMessage {

    private final String stickerId;

    public StickerIncomingMessage(Update update) {
        super(update);

        this.stickerId = update.getMessage().getSticker().getFileId();
    }

}
