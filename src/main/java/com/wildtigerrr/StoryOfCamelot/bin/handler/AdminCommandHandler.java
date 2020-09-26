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
            case BAN: ban((TextIncomingMessage) message);break;
            case ID: sendIdsToAdminChannel(message.getUserId(), message.getChatId()); break;
            case TEST: test(message); break;
        }
    }

    private void test(IncomingMessage message) {
        if (!isAdmin(message.getUserId())) {
            defaultCommandHandler.process(message);
            return;
        }
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text("[Тест](https://t.me/StoryOfCamelotBot?start=test)")
                .applyMarkup(true)
                .build());
    }

    private void ban(TextIncomingMessage message) {
        if (!isAdmin(message.getUserId())) {
            defaultCommandHandler.process(message);
            return;
        }
        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, message.getPlayer().getId());
        cacheService.add(CacheType.PLAYER_STATE, state.ban());
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text("User banned")
                .build()
        );
    }

    private void sendIdsToAdminChannel(String userId, long chatId) {
        messages.postMessageToAdminChannel("User Id: " + userId + ", Chat Id: " + chatId);
    }

    private Boolean isAdmin(String userId) {
        return userId.equals(BotConfig.WEBHOOK_ADMIN_ID);
    }

}
