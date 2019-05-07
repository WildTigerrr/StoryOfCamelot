package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMovement;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.GameSettings;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.exceptions.SOCInvalidDataException;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.FileLinkServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.service.AmazonClient;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@Service
public class ResponseHandler {

    @Autowired
    private GameMain gameMain;
    @Autowired
    private PlayerServiceImpl playerService;
    @Autowired
    private FileLinkServiceImpl fileLinkService;
    @Autowired
    private LocationServiceImpl locationService;
    @Autowired
    private FileProcessing imageService;
    @Autowired
    private ResponseManager messages;
    @Autowired
    private GameMovement movementService;
    private AmazonClient amazonClient;

    public ResponseHandler() {
    }

    @SuppressWarnings("unused")
    @Autowired
    ResponseHandler(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    void handleMessage(UpdateWrapper message) {
        message.setPlayer(getPlayer(message.getUserId()));
        System.out.println("Working with message: " + message);
//        if (message.getPlayer().getS);
        logSender(message);
        if (message.getUserId().equals(BotConfig.WEBHOOK_ADMIN_ID)) {
            if (message.getText().equals("database test")) {
                // Some admin actions
                String locationName = "Test Forest";
                Location newLocation = locationService.findByName(locationName);
                if (newLocation != null) {
                    messages.sendMessage(newLocation.toString(), message.getUserId());
                } else {
                    messages.sendMessageToAdmin("Searched location didn't found: " + locationName);
                    System.out.println("No such location");
                }
            } else if (message.getText().equals("image test")) {
                sendTestImage(message.getUserId());
                return;
            }
        }
        if (message.getText().startsWith("/")) {
            if (message.getText().equals("/count")) {
                TimeDependentActions.addCount();
                return;
            }
            performCommand(message);
            return;
        }
        Player player = message.getPlayer();
        if (message.getPlayer().isNew()) {
            System.out.println("New player");
            player.setup();
            playerService.update(player);
            messages.sendMessage(MainText.MEET_NEW_PLAYER.text(), message.getUserId(), true);
        } else if (player.getExternalId().equals(player.getNickname())) {
            System.out.println("Here should be nickname set");
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

    private void performCommand(UpdateWrapper message) {
        String[] commandParts = message.getText().split(" ", 2);
        Command command;
        try {
            command = Command.valueOf(commandParts[0].substring(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            messages.sendMessage(MainText.UNKNOWN_COMMAND.text(), message.getUserId(), true);
            return;
        }
        switch (command) {
            case ME:
                messages.sendMessage(playerService.getPlayerInfo(message.getUserId()), message.getUserId(), true);
                break;
            case NICKNAME:
                if (commandParts.length > 1) {
                    setNickname(message.getPlayer(), commandParts[1]);
                } else {
                    messages.sendMessage(MainText.EMPTY_NICKNAME.text(), message.getUserId(), true);
                }
                break;
            case ADD:
                String[] values = commandParts[1].split(" ", 2);
                Player player = gameMain.addExperience(message.getPlayer(), Stats.valueOf(values[0].toUpperCase()), Integer.parseInt(values[1]), true);
                playerService.update(player);
                break;
            case ACTION:
                if (commandParts.length <= 1) return;
                commandParts = message.getText().split(" ", 3);
                switch (commandParts[1]) {
                    case "init":
                        TimeDependentActions.initList();
                        break;
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
                if (commandParts.length <= 1) {
//                    sendMessage(locationService.getAll().toString(), message.getUserId());
                    movementService.sendAvailableLocations(message.getPlayer());
                } else if (message.isQuery()) {
                    movementService.moveToLocation(message, commandParts[1]);
                }
                break;
            default:
                messages.sendMessage(MainText.COMMAND_NOT_DEFINED.text(), message.getUserId(), true);
        }
    }

    private Player getPlayer(String externalId) {
        Player player;
        player = playerService.findByExternalId(externalId);
        if (player == null) {
            player = new Player(externalId, externalId, locationService.findByName(GameSettings.DEFAULT_LOCATION.get()));
            player = playerService.create(player);
        }
        return player;
    }

    private void setNickname(Player player, String newName) {
        player.setNickname(newName);
        playerService.update(player);
        messages.sendMessage(MainText.NICKNAME_CHANGED.text() + player.getNickname() + "*", player.getExternalId(), true);
    }

}
