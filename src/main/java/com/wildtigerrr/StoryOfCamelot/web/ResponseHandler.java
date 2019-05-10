package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMovement;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameTutorial;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButtons;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
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
        if (message.getUserId().equals(BotConfig.WEBHOOK_ADMIN_ID)) {
            if (performAdminCommands(message)) return;
        }
        if (performCommand(message)) {
            return;
        }
        Player player = message.getPlayer();
        if (message.getPlayer().isNew()) {
            tutorial.tutorialStart(message.getPlayer());
            return;
        } else if (player.getExternalId().equals(player.getNickname())) {
            gameMain.setNickname(message.getPlayer(), message.getText());
            return;
        }
        String answer = "You wrote me: " + message.getText();
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
        String log = "New message, User:"
                + (message.getFirstName() == null ? "" : " " + message.getFirstName())
                + (message.getLastName() == null ? "" : " " + message.getLastName())
                + " (id" + message.getUserId() + ")"
                + (message.getUsername() == null ? "" : ", also known as " + message.getUsername())
                + ", wrote a message: " + message.getText();
        System.out.println(log);

        if (!message.getUserId().equals(BotConfig.WEBHOOK_ADMIN_ID)) {
            messages.sendMessageToAdmin(log);
        }
    }

    private Boolean performAdminCommands(UpdateWrapper message) {
        if (message.getText().equals("image test")) {
            sendTestImage(message.getUserId());
            return true;
        }
        return false;
    }

    private Boolean performCommand(UpdateWrapper message) {
        String[] commandParts = message.getText().split(" ", 2);
        Command command;
        if (message.getText().startsWith("/")) {
            try {
                command = Command.valueOf(commandParts[0].substring(1).toUpperCase());
            } catch (IllegalArgumentException e) {
                messages.sendMessage(MainText.UNKNOWN_COMMAND.text(), message.getUserId(), true);
                return false;
            }
        } else {
            command = buttonToCommand(message.getText());
            if (command == null) {
                return false;
            }
        }
        switch (command) {
            case ME:
                messages.sendMessage(playerService.getPlayerInfo(message.getUserId()), message.getUserId(), true);
                if (message.getPlayer().getAdditionalStatus() == PlayerStatusExtended.TUTORIAL_STATS) tutorial.tutorialStats(message.getPlayer());
                break;
            case SKILLS:
                messages.sendMessage(MainText.COMMAND_NOT_DEVELOPED.text(), message.getUserId(), true);
                break;
            case NICKNAME:
                if (commandParts.length > 1) {
                    gameMain.setNickname(message.getPlayer(), commandParts[1]);
                } else {
                    messages.sendMessage(MainText.NICKNAME_EMPTY.text(), message.getUserId(), true);
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
                if (!message.isQuery() || commandParts.length < 2) {
                    movementService.sendAvailableLocations(message.getPlayer());
                } else {
                    movementService.moveToLocation(message, commandParts[1]);
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
                    messages.sendMessage(MainText.PROPOSITION_EXPIRED.text(), message.getUserId());
                }
                return true;
            default:
                messages.sendMessage(MainText.COMMAND_NOT_DEFINED.text(), message.getUserId(), true);
                return false;
        }
        return true;
    }

    private Command buttonToCommand(String text) {
        if (text.equals(ReplyButtons.MOVE.getLabel())) return Command.MOVE;
        else if (text.equals(ReplyButtons.ME.getLabel())) return Command.ME;
        else if (text.equals(ReplyButtons.SKILLS.getLabel())) return Command.SKILLS;
        return null;
    }

}