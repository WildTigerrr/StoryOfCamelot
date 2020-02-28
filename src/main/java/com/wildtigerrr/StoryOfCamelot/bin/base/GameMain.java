package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.template.PlayerService;
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
    private final PlayerService playerService;
    private final TranslationManager translation;
    private final GameTutorial tutorial;
    private final FileProcessing imageService;

    @Autowired
    public GameMain(
            ResponseManager messages,
            PlayerServiceImpl playerService,
            TranslationManager translation,
            GameTutorial tutorial,
            FileProcessing imageService
    ) {
        this.messages = messages;
        this.playerService = playerService;
        this.translation = translation;
        this.tutorial = tutorial;
        this.imageService = imageService;
    }

    public void handleTextMessage(UpdateWrapper message) {
        setPlayerToMessage(message);
        logSender(message);
        executeCommand(message);
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

    public void sendTopPlayers(String currentPlayerId) {
        List<Player> players = playerService.getTopPlayers(10);
        AtomicInteger index = new AtomicInteger();
        String top = "Топ игроков: \n\n" +
                players.stream()
                        .map(pl -> pl.toStatString(index.incrementAndGet()))
                        .collect(Collectors.joining());
        messages.sendMessage(TextResponseMessage.builder()
                .text(top).targetId(currentPlayerId).build()
        );
    }

    private void executeCommand(UpdateWrapper message) {
        if (executeAdminCommand(message)) return;
        else if (executePlayerCommand(message)) return;
        else commandNotExecuted(message);
    }

    private boolean executeAdminCommand(UpdateWrapper message) {
        return message.getUserId().equals(BotConfig.WEBHOOK_ADMIN_ID) && performAdminCommands(message);
    }

    private Boolean performAdminCommands(UpdateWrapper message) {
        switch (message.getText()) {
            case "image test":
                sendTestImage(message.getUserId());
                return true;
            case "/tutorial off": {
                disableTutorial(message.getPlayer());
                return true;
            }
            case "/tutorial on": {
                enableTutorial(message.getPlayer());
                return true;
            }
            case "/id": {
                sendIdsToAdminChannel(message.getUserId(), message.getChatId());
                return true;
            }
        }
        if (message.getText().startsWith("/ping")) {
            messages.postMessageToAdminChannel(message.getText(), true);
            return true;
        }
        return false;
    }

    private Boolean executePlayerCommand(UpdateWrapper message) {
        if (message.getPlayer().getStatus() == PlayerStatus.TUTORIAL && tutorial.proceedTutorial(message)) return true;

        Command command = message.getCommand();
        if (command == null) return false;
        String[] commandParts = message.getText().split(" ", 2);
        if (command == Command.ACTION) {
            proceedTimeAction(message.getText(), commandParts);
            return true;
        } else if (command == Command.START) {
            startGame(message);
            return true;
        } else {
            return command.execute(message);
        }
    }

    private void commandNotExecuted(UpdateWrapper message) {
        String answer = "Я не знаю как это обработать: " + message.getText();
        log.debug("Answer: " + answer);
        messages.sendMessage(TextResponseMessage.builder()
                .text(answer).targetId(message).build()
        );
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

    private void disableTutorial(Player player) {
        player.activate();
        playerService.update(player);
        messages.sendMessage(TextResponseMessage.builder()
                .text("Туториал отключен").targetId(player).build()
        );
    }

    private void enableTutorial(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_NICKNAME);
        player.stop();
        playerService.update(player);
        messages.sendMessage(TextResponseMessage.builder()
                .text("Туториал перезапущен").targetId(player).build()
        );
    }

    private void sendIdsToAdminChannel(String userId, long chatId) {
        messages.postMessageToAdminChannel("User Id: " + userId + ", Chat Id: " + chatId);
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

    private void startGame(UpdateWrapper message) {
        if (message.getPlayer().isNew()) {
            tutorial.tutorialStart(message.getPlayer());
        } else {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(translation.getMessage("commands.expired", message)).targetId(message).build()
            );
        }
    }

    private void setPlayerToMessage(UpdateWrapper message) {
        message.setPlayer(playerService.getPlayer(message.getUserId()));
    }

    public void sendMessageToUser(String message) {
        String[] commandParts = message.split(" ", 3);
//        Player receiver = playerService.findByExternalId(commandParts[1]);
        messages.sendMessage(TextResponseMessage.builder()
                .text(commandParts[2]).targetId(commandParts[1]).build()
        );
        //                    messages.sendMessage("Пользователь не найден", message.getUserId());
    }

    public void sendDumb(UpdateWrapper update) {
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("commands.not-defined", update))
                .targetId(update)
                .applyMarkup(true).build()
        );
    }

    private void logSender(UpdateWrapper message) {
        log.debug("Working with message: " + message);
        if (!message.getUserId().equals(BotConfig.WEBHOOK_ADMIN_ID)) {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(message.toString()).type(ResponseType.POST_TO_ADMIN_CHANNEL).build()
            );
        }
    }

    private void handleError(String message, Exception e) {
        log.error(message, e);
        messages.postMessageToAdminChannel(message);
    }

}
