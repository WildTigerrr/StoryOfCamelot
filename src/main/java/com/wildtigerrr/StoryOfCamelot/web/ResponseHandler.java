package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMovement;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameTutorial;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ResponseHandler {

    private static final Logger log = LogManager.getLogger(ResponseHandler.class);

    @Autowired
    private GameMain gameMain;
    @Autowired
    private GameTutorial tutorial;
    @Autowired
    private PlayerServiceImpl playerService;
    @Autowired
    private FileProcessing imageService;
    @Autowired
    private ResponseManager messages;
    @Autowired
    private GameMovement movementService;
    @Autowired
    private TranslationManager translation;

    void handleMessage(UpdateWrapper message) {
        message.setPlayer(gameMain.getPlayer(message.getUserId()));
        logSender(message);
        if (message.getUserId().equals(BotConfig.WEBHOOK_ADMIN_ID) && performAdminCommands(message)) return;
        else if (performCommand(message)) return;
        else if (message.getPlayer().getExternalId().equals(message.getPlayer().getNickname())) {
            gameMain.setNickname(message.getPlayer(), message.getText());
            return;
        }
        String answer = "Я не знаю как это обработать: " + message.getText();
        log.debug("Answer: " + answer);
        messages.sendMessage(answer, message.getUserId(), false);
    }

    private void sendTestImage(String userId) {
        messages.sendMessage("Нужно бы забраться повыше и осмотреться...", userId);
        String docName = "Test name";
        try {
            InputStream result = imageService.overlayImages(
                    "images/locations/forest-test.png",
                    "images/items/weapons/swords/sword-test.png"
            );
            File file = imageService.inputStreamToFile(result, docName, ".png");
            if (file != null) {
                messages.sendImage(file, userId);
            }
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
                Player player = message.getPlayer();
                player.activate();
                playerService.update(player);
                messages.sendMessage("Туториал отключен", message.getUserId());
                return true;
            }
            case "/tutorial on": {
                Player player = message.getPlayer();
                player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_NICKNAME);
                player.stop();
                playerService.update(player);
                messages.sendMessage("Туториал перезапущен", message.getUserId());
                return true;
            }
            case "/id": {
                messages.postMessageToAdminChannel("User Id: " + message.getUserId() + ", Chat Id: " + message.getChatId());
                return true;
            }
        }
        if (message.getText().startsWith("/ping")) {
            messages.postMessageToAdminChannel(message.getText(), true);
            return true;
        }
        return false;
    }

    private Boolean performCommand(UpdateWrapper message) {
        if (message.getPlayer().getStatus() == PlayerStatus.TUTORIAL && tutorial.proceedTutorial(message)) return true;

        Command command = messageToCommand(message.getText(), message.getPlayer().getLanguage());
        if (command == null) return false;
        String[] commandParts = message.getText().split(" ", 2);
        switch (command) {
            case ME:
                messages.sendMessage(playerService.getPlayerInfo(message.getUserId(), message.getPlayer().getLanguage()), message.getUserId(), true);
                break;
            case SKILLS:
                gameMain.sendSkillWindow(message.getPlayer());
                break;
            case FIGHT:
                gameMain.fight(message);
                break;
            case NICKNAME:
                if (commandParts.length > 1) {
                    gameMain.setNickname(message.getPlayer(), commandParts[1]);
                } else {
                    messages.sendMessage(translation.get(message.getPlayer().getLanguage()).nicknameEmpty(), message.getUserId(), true); // MainText.NICKNAME_EMPTY.text(message.getPlayer().getLanguage())
                }
                break;
            case ADD:
                String[] values = commandParts[1].split(" ", 2);
                try {
                    Player player = gameMain.addExperience(
                            message.getPlayer(),
                            Stats.valueOf(values[0].toUpperCase()),
                            Integer.parseInt(values[1]),
                            true
                    );
                    playerService.update(player);
                } catch (IllegalArgumentException e) {
                    handleError("Wrong Stat" + values[0].toUpperCase(), e);
                }
                break;
            case ACTION:
                if (commandParts.length <= 1) return true;
                commandParts = message.getText().split(" ", 3);
                switch (commandParts[1]) {
                    case "add":
                        TimeDependentActions.addElement(commandParts[2]);
                        break;
                    case "remove":
                        TimeDependentActions.removeFirst();
                        break;
                    case "get":
                        TimeDependentActions.getAll();
                        break;
                }
                break;
            case MOVE:
                movementService.handleMove(message);
                break;
            case SEND:
                commandParts = message.getText().split(" ", 3);
                Player receiver = playerService.findByExternalId(commandParts[1]);
                if (receiver != null) {
                    messages.sendMessage(commandParts[2], commandParts[1]);
                } else {
                    messages.sendMessage(commandParts[2], commandParts[1]);
//                    messages.sendMessage("Пользователь не найден", message.getUserId());
                }
                break;
            case START:
                if (message.getPlayer().isNew()) {
                    tutorial.tutorialStart(message.getPlayer());
                } else {
                    messages.sendMessage(translation.get(message.getPlayer().getLanguage()).propositionExpired(), message.getUserId()); // MainText.PROPOSITION_EXPIRED.text(message.getPlayer().getLanguage())
                }
                break;
            case UP:
                gameMain.statUp(message);
                break;
            case TOP:
                gameMain.getTopPlayers(message.getUserId());
                break;
            default:
                messages.sendMessage(translation.get(message.getPlayer().getLanguage()).commandNotDefined(), message.getUserId(), true); // MainText.COMMAND_NOT_DEFINED.text(message.getPlayer().getLanguage())
                return false;
        }
        return true;
    }

    public static Command messageToCommand(String text, Language lang) {
        if (text == null || text.length() == 0) return null;
        if (text.startsWith("/")) {
            try {
                String[] commandParts = text.split(" ", 2);
                return Command.valueOf(commandParts[0].substring(1).toUpperCase());
            } catch (IllegalArgumentException e) {
                if (text.startsWith("/up")) return Command.UP;
                return null;
            }
        } else {
            return ReplyButton.buttonToCommand(text, lang);
        }
    }

    private void handleError(String message, Exception e) {
        log.error(message, e);
        messages.postMessageToAdminChannel(message);
    }

}