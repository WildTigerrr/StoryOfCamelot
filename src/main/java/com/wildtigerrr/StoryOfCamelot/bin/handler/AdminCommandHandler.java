package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.ImageResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Log4j2
public class AdminCommandHandler extends TextMessageHandler {

    private final CacheProvider cacheService;
    private final DefaultCommandHandler defaultCommandHandler;
    private final FileProcessing imageService;

    public AdminCommandHandler(ResponseManager messages, TranslationManager translation, CacheProvider cacheService, DefaultCommandHandler defaultCommandHandler, FileProcessing imageService) {
        super(messages, translation);
        this.cacheService = cacheService;
        this.defaultCommandHandler = defaultCommandHandler;
        this.imageService = imageService;
    }

    @Override
    public void process(IncomingMessage message) {
        switch (message.getCommand()) {
            case BAN: ban((TextIncomingMessage) message);break;
            case ID: sendIdsToAdminChannel(message.getUserId(), message.getChatId()); break;
            case TEST: test(message); break;
            case PING: ping(message); break;
        }
    }

    private void ping(IncomingMessage message) {
        messages.postMessageToAdminChannel(message.text(), true);
    }

    private void test(IncomingMessage message) {
        if (!isAdmin(message.getUserId())) {
            defaultCommandHandler.process(message);
            return;
        }
        if (message.text().equals("/test image")) {
            sendTestImage(message.getUserId());
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

    private void sendTestImage(String userId) {
        messages.sendMessage(TextResponseMessage.builder()
                .text("Нужно бы забраться повыше и осмотреться...").targetId(userId).build()
        );
        String docName = "Test name";
        try {
            File file = imageService.getOverlaidImagesAsFile(
                    "images/locations/forest-test.png",
                    "images/items/weapons/swords/sword-test.png",
                    docName,
                    ".png"
            );
            messages.sendMessage(ImageResponseMessage.builder()
                    .file(file).targetId(userId).build()
            );
        } catch (IOException e) {
            handleError(e.getMessage(), e);
        }
    }

//    private void proceedTimeAction(String message, String[] commandParts) {
//        if (commandParts.length <= 1) return;
//        commandParts = message.split(" ", 3);
//        switch (commandParts[1]) {
//            case "add":
//                TimeDependentActions.addElement(commandParts[2]);
//                break;
//            case "remove":
//                TimeDependentActions.removeFirst();
//                break;
//            case "get":
//                messages.postMessageToAdminChannel(TimeDependentActions.getAll());
//                break;
//        }
//    }

    private Boolean isAdmin(String userId) {
        return userId.equals(BotConfig.WEBHOOK_ADMIN_ID);
    }

    private void handleError(String message, Exception e) {
        log.error(message, e);
        messages.postMessageToAdminChannel(message);
    }

}
