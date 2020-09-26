package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.CharacterStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.BackpackService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.ItemService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.bot.utils.UpdateWrapperUtils;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.ImageResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.StickerResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
@Service
public class GameMain {

    private final ResponseManager messages;
    private final FileProcessing imageService;

    @Autowired
    public GameMain(
            ResponseManager messages,
            FileProcessing imageService
    ) {
        this.messages = messages;
        this.imageService = imageService;
    }

    @Transactional
    public void handleTextMessage(UpdateWrapper message) {
    }

    public void handleImageMessage(Update update) {
        messages.sendMessage(
                ImageResponseMessage.builder()
                        .targetId(BotConfig.ADMIN_CHANNEL_ID)
                        .caption(UpdateWrapperUtils.getUpdateLogCaption(update))
                        .fileId(UpdateWrapperUtils.getBiggestPhotoId(update))
                        .build()
        );
    }

    public void handleStickerMessage(Update update) {
        messages.sendMessage(
                StickerResponseMessage.builder()
                        .targetId(BotConfig.ADMIN_CHANNEL_ID)
                        .fileId(update.getMessage().getSticker().getFileId())
                        .build()
        );
    }

    public void handleUnsupportedMessage(Update update) {
        log.error("Message not supported: " + update.toString());
        messages.postMessageToAdminChannel("Message not supported: " + update.toString());
    }

    private Boolean performAdminCommands(UpdateWrapper message) {
        switch (message.getText()) {
            case "image test":
                sendTestImage(message.getUserId());
                return true;
        }
        if (message.getText().startsWith("/ping")) {
            messages.postMessageToAdminChannel(message.getText(), true);
            return true;
        }
        return false;
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

    private void proceedTimeAction(String message, String[] commandParts) {
        if (commandParts.length <= 1) return;
        commandParts = message.split(" ", 3);
        switch (commandParts[1]) {
            case "add":
                TimeDependentActions.addElement(commandParts[2]);
                break;
            case "remove":
                TimeDependentActions.removeFirst();
                break;
            case "get":
                messages.postMessageToAdminChannel(TimeDependentActions.getAll());
                break;
        }
    }

    public void sendMessageToUser(String message) {
        String[] commandParts = message.split(" ", 3);
//        Player receiver = playerService.findByExternalId(commandParts[1]);
        messages.sendMessage(TextResponseMessage.builder()
                .text(commandParts[2]).targetId(commandParts[1]).build()
        );
        //                    messages.sendMessage("Пользователь не найден", message.getUserId());
    }

    private void handleError(String message, Exception e) {
        log.error(message, e);
        messages.postMessageToAdminChannel(message);
    }

}
