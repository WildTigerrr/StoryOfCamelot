package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMovement;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameTutorial;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ResponseHandler {

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

    void handleMessage(UpdateWrapper message) {
        message.setPlayer(gameMain.getPlayer(message.getUserId()));
        System.out.println("Working with message: " + message);
//        if (message.getPlayer().getS);
        logSender(message);
        if (message.getUserId().equals(BotConfig.WEBHOOK_ADMIN_ID) && performAdminCommands(message)) return;
        else if (performCommand(message)) return;
        else if (message.getPlayer().getExternalId().equals(message.getPlayer().getNickname())) {
            gameMain.setNickname(message.getPlayer(), message.getText());
            return;
        }
        String answer = "Я не знаю как это обработать: " + message.getText();
        System.out.println("Answer: " + answer);
        messages.sendMessage(answer, message.getUserId(), false);
    }

    private void sendTestImage(String userId) {
        messages.sendMessage("Нужно бы забраться повыше и осмотреться...", userId);
        String docName = "Test name";
        InputStream result = null;
        try {
            result = imageService.overlayImages(
                    imageService.getFile("images/locations/forest-test.png"),
                    imageService.getFile("images/items/weapons/swords/sword-test.png")
            );
        } catch (IOException e) {
            messages.sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
        File file = null;
        try {
            file = imageService.inputStreamToFile(result, docName, ".png");
        } catch (IOException e) {
            messages.sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
        if (file != null) {
//            sendDocument(file, userId);
            messages.sendImage(file, userId);
        }
    }

    private void logSender(UpdateWrapper message) {
        if (!message.getUserId().equals(BotConfig.WEBHOOK_ADMIN_ID)) {
            messages.sendMessageToAdmin(message.toString());
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
                messages.sendMessage(playerService.getPlayerInfo(message.getUserId()), message.getUserId(), true);
                break;
            case SKILLS:
                messages.sendMessage(MainText.COMMAND_NOT_DEVELOPED.text(message.getPlayer().getLanguage()), message.getUserId(), true);
                break;
            case NICKNAME:
                if (commandParts.length > 1) {
                    gameMain.setNickname(message.getPlayer(), commandParts[1]);
                } else {
                    messages.sendMessage(MainText.NICKNAME_EMPTY.text(message.getPlayer().getLanguage()), message.getUserId(), true);
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
                    messages.sendMessageToAdmin("Не верная характеристика: " + values[0].toUpperCase());
                    System.out.println("Error, IllegalArgumentException - Не верная характеристика: " + values[0].toUpperCase());
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
                if (message.getPlayer().getStatus() == PlayerStatus.MOVEMENT) {
                    if (message.isQuery()) {
                        messages.sendMessageEdit(
                                message.getMessageId(),
                                MainText.ALREADY_MOVING.text(message.getPlayer().getLanguage()),
                                message.getUserId(),
                                true
                        );
                    } else {
                        messages.sendMessage(MainText.ALREADY_MOVING.text(message.getPlayer().getLanguage()), message.getUserId());
                    }
                } else if (message.isQuery()) {
                    movementService.moveToLocation(message, commandParts[1]);
                } else { // if (commandParts.length < 2)
                    movementService.sendAvailableLocations(message.getPlayer());
                }
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
                    messages.sendMessage(MainText.PROPOSITION_EXPIRED.text(message.getPlayer().getLanguage()), message.getUserId());
                }
                return true;
            case UP:
                gameMain.statUp(message);
                return true;
            default:
                messages.sendMessage(MainText.COMMAND_NOT_DEFINED.text(message.getPlayer().getLanguage()), message.getUserId(), true);
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

}