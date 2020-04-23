package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.ImageIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.ImageResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

@Service
public class ImageMessageHandler extends CommandHandler {

    protected ImageMessageHandler(ResponseManager messages, TranslationManager translation) {
        super(messages, translation);
    }

    @Override
    public void process(IncomingMessage message) {
        ImageIncomingMessage imageMessage = (ImageIncomingMessage) message;
        messages.sendMessage(ImageResponseMessage.builder().lang(message)
                .targetId(BotConfig.ADMIN_CHANNEL_ID)
                .caption(imageMessage.getAuthorCaption())
                .fileId(imageMessage.getBiggetImageId())
                .build()
        );
        messages.sendMessage(TextResponseMessage.builder().lang(message)
                .text(translation.getMessage("commands.unknown", message))
                .targetId(message)
                .build()
        );
    }

}
