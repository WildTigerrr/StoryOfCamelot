package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMovement;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameTutorial;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.bot.utils.UpdateWrapperUtils;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;

@Log4j2
@Service
public class ResponseHandler {

    private final GameMain gameMain;
    private final GameTutorial tutorial;
    private final PlayerServiceImpl playerService;
    private final FileProcessing imageService;
    private final ResponseManager messages;
    private final GameMovement movementService;
    private final TranslationManager translation;

    @Autowired
    public ResponseHandler(
            GameMain gameMain,
            GameTutorial tutorial,
            PlayerServiceImpl playerService,
            FileProcessing imageService,
            ResponseManager messages,
            GameMovement movementService,
            TranslationManager translation
    ) {
        this.gameMain = gameMain;
        this.tutorial = tutorial;
        this.playerService = playerService;
        this.imageService = imageService;
        this.messages = messages;
        this.movementService = movementService;
        this.translation = translation;
    }

    void handleUpdate(Update update) {
        UpdateWrapper message = new UpdateWrapper(update);
        if (message.isCommand()) handleTextMessage(message);
        else if (update.hasMessage() && update.getMessage().hasPhoto()) handleImageMessage(update);
        else handleUnsupportedMessage(update);
    }

    void handleTextMessage(UpdateWrapper message) {
        setPlayerToMessage(message);
        logSender(message);
        executeCommand(message);
    }

    void handleImageMessage(Update update) {
        messages.sendMessage(
                ResponseMessage.builder()
                        .type(ResponseType.PHOTO)
                        .targetId(BotConfig.ADMIN_CHANNEL_ID)
                        .text(UpdateWrapperUtils.getUpdateLogCaption(update))
                        .file(ResponseMessage.addFile()
                                .fileId(UpdateWrapperUtils.getBiggestPhotoId(update))
                                .build())
                        .build()
        );

        messages.sendImage(
                UpdateWrapperUtils.getBiggestPhotoId(update),
                BotConfig.ADMIN_CHANNEL_ID,
                update.getMessage().getCaption() != null ?
                        update.getMessage().getCaption() + ", " + UpdateWrapperUtils.getUpdateAuthorCaption(update)
                        : UpdateWrapperUtils.getUpdateAuthorCaption(update)
        );
    }

    void handleUnsupportedMessage(Update update) {
        log.error("Message not supported: " + update.toString());
        messages.postMessageToAdminChannel("Message not supported: " + update.toString());
    }

    private void setPlayerToMessage(UpdateWrapper message) {
        message.setPlayer(gameMain.getPlayer(message.getUserId()));
    }

    private void executeCommand(UpdateWrapper message) {
        if (executeAdminCommand(message)) return;
        else if (executePlayerCommand(message)) return;
        else commandNotExecuted(message);
    }

    private boolean executeAdminCommand(UpdateWrapper message) {
        return message.getUserId().equals(BotConfig.WEBHOOK_ADMIN_ID) && performAdminCommands(message);
    }

    private void commandNotExecuted(UpdateWrapper message) {
        String answer = "Я не знаю как это обработать: " + message.getText();
        log.debug("Answer: " + answer);
        messages.sendMessage(answer, message.getUserId(), false);
    }

    private void sendTestImage(String userId) {
        messages.sendMessage("Нужно бы забраться повыше и осмотреться...", userId);
        String docName = "Test name";
        try {
            File file = imageService.getOverlaidImagesAsFile(
                    "images/locations/forest-test.png",
                    "images/items/weapons/swords/sword-test.png",
                    docName,
                    ".png"
            );
            messages.sendImage(file, userId);
        } catch (IOException e) {
            handleError(e.getMessage(), e);
        }
    }

    private void logSender(UpdateWrapper message) {
        log.debug("Working with message: " + message);
        if (!message.getUserId().equals(BotConfig.WEBHOOK_ADMIN_ID)) {
            messages.postMessageToAdminChannel(message.toString());
        }
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
        switch (command) {
            case ME:
                sendPlayerInfo(message);
                break;
            case SKILLS:
                gameMain.sendSkillWindow(message.getPlayer());
                break;
            case FIGHT:
                gameMain.fight(message);
                break;
            case NICKNAME:
                updateNickname(message, commandParts);
                break;
            case ADD:
                addStatPoints(message.getPlayer(), commandParts);
                break;
            case ACTION:
                proceedTimeAction(message.getText(), commandParts);
                break;
            case MOVE:
                movementService.handleMove(message);
                break;
            case SEND:
                sendMessageToUser(message.getText(), commandParts);
                break;
            case START:
                startGame(message);
                break;
            case UP:
                gameMain.statUp(message);
                break;
            case TOP:
                gameMain.getTopPlayers(message.getUserId());
                break;
            default:
                messages.sendMessage(translation.getMessage("commands.not-defined", message), message.getUserId(), true);
                return false;
        }
        return true;
    }

    private void disableTutorial(Player player) {
        player.activate();
        playerService.update(player);
        messages.sendMessage("Туториал отключен", player.getExternalId());
    }

    private void enableTutorial(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_NICKNAME);
        player.stop();
        playerService.update(player);
        messages.sendMessage("Туториал перезапущен", player.getExternalId());
    }

    private void sendIdsToAdminChannel(String userId, long chatId) {
        messages.postMessageToAdminChannel("User Id: " + userId + ", Chat Id: " + chatId);
    }

    private void sendPlayerInfo(UpdateWrapper message) {
        messages.sendMessage(
                playerService.getPlayerInfo(
                        message.getUserId(),
                        message.getPlayer().getLanguage()
                ),
                message.getUserId(),
                true
        );
    }

    private void updateNickname(UpdateWrapper message, String[] commandParts) {
        if (commandParts.length > 1) {
            gameMain.setNickname(message.getPlayer(), commandParts[1]);
        } else {
            messages.sendMessage(translation.getMessage("player.nickname.empty", message), message.getUserId(), true);
        }
    }

    private void addStatPoints(Player player, String[] commandParts) {
        String[] values = commandParts[1].split(" ", 2);
        try {
            player = gameMain.addExperience(
                    player,
                    Stats.valueOf(values[0].toUpperCase()),
                    Integer.parseInt(values[1]),
                    true
            );
            playerService.update(player);
        } catch (IllegalArgumentException e) {
            handleError("Wrong Stat" + values[0].toUpperCase(), e);
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

    private void sendMessageToUser(String message, String[] commandParts) {
        commandParts = message.split(" ", 3);
        Player receiver = playerService.findByExternalId(commandParts[1]);
        if (receiver != null) {
            messages.sendMessage(commandParts[2], commandParts[1]);
        } else {
            messages.sendMessage(commandParts[2], commandParts[1]);
//                    messages.sendMessage("Пользователь не найден", message.getUserId());
        }
    }

    private void startGame(UpdateWrapper message) {
        if (message.getPlayer().isNew()) {
            tutorial.tutorialStart(message.getPlayer());
        } else {
            messages.sendMessage(translation.getMessage("commands.expired", message), message.getUserId());
        }
    }

    private void handleError(String message, Exception e) {
        log.error(message, e);
        messages.postMessageToAdminChannel(message);
    }

}