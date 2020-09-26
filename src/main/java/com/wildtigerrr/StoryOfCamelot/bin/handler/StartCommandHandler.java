package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.ActionHandler;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import org.springframework.stereotype.Service;

@Service
public class StartCommandHandler extends TextMessageHandler {

    private final ActionHandler actionHandler;
    private final LanguageCommandHandler languageCommandHandler;
    private final NicknameCommandHandler nicknameCommandHandler;

    public StartCommandHandler(ResponseManager messages, TranslationManager translation, ActionHandler actionHandler, LanguageCommandHandler languageCommandHandler, NicknameCommandHandler nicknameCommandHandler) {
        super(messages, translation);
        this.actionHandler = actionHandler;
        this.languageCommandHandler = languageCommandHandler;
        this.nicknameCommandHandler = nicknameCommandHandler;
    }

    @Override
    public void process(IncomingMessage message) {
        Player player = message.getPlayer();
        // TODO Not set default language
        if (player.getLanguage() == null) {
            languageCommandHandler.process(message);
        } else if (!message.text().startsWith("/start") && player.getNickname().equals(player.getExternalId())) {
            nicknameCommandHandler.process(message);
        } else {
            actionHandler.sendAvailableActions(player);
        }
    }

}
