package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

@Service
public class AdminCommandHandler extends TextMessageHandler {

    private final CacheProvider cacheService;
    private final DefaultCommandHandler defaultCommandHandler;

    public AdminCommandHandler(ResponseManager messages, TranslationManager translation, CacheProvider cacheService, DefaultCommandHandler defaultCommandHandler) {
        super(messages, translation);
        this.cacheService = cacheService;
        this.defaultCommandHandler = defaultCommandHandler;
    }

    @Override
    public void process(IncomingMessage message) {
        switch (message.getCommand()) {
            case BAN:
                ban((TextIncomingMessage) message);
                break;
        }
    }

    private void ban(TextIncomingMessage message) {
        if (isAdmin(message.getUserId())) {
            PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, message.getPlayer().getId());
            cacheService.add(CacheType.PLAYER_STATE, state.ban());
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("User banned")
                    .build()
            );
        } else {
            defaultCommandHandler.process(message);
        }
    }

    private Boolean isAdmin(String userId) {
        return userId.equals(BotConfig.WEBHOOK_ADMIN_ID);
    }

}
