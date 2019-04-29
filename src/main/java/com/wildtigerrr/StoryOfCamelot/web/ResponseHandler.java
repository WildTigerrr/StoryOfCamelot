package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.Command;
import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.bin.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.exceptions.SOCInvalidDataException;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.*;
import com.wildtigerrr.StoryOfCamelot.web.service.TimeDependentActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.ArrayList;

import static com.wildtigerrr.StoryOfCamelot.web.BotConfig.WEBHOOK_ADMIN_ID;

@Service
public class ResponseHandler {

    @Autowired private WebHookHandler webHook;
    @Autowired private PlayerServiceImpl playerService;
    @Autowired private FileLinkServiceImpl fileLinkService;
    @Autowired private LocationServiceImpl locationService;
    @Autowired private FileProcessing imageService;
//    private AmazonClient amazonClient;
//
//    public ResponseHandler() {}
//
//    @SuppressWarnings("unused")
//    @Autowired
//    ResponseHandler(AmazonClient amazonClient) {
//        this.amazonClient = amazonClient;
//    }

    void handleMessage(UpdateWrapper message) {
        System.out.println("Working with message: " + message);
        logSender(message);
        message.setPlayer(getPlayer(message.getUserId()));
        if (message.getUserId().equals(WEBHOOK_ADMIN_ID)) {
            if (message.getText().equals("database test")) {
                // Some admin actions
                String locationName = "Test Forest";
                Location newLocation = locationService.findByName(locationName);
                if (newLocation != null) {
                    sendMessage(newLocation.toString(), message.getUserId());
                } else {
                    sendMessageToAdmin("Searched location didn't found: " + locationName);
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
            sendMessage(MainText.MEET_NEW_PLAYER.text(), message.getUserId(), true);
        } else if (player.getExternalId().equals(player.getNickname())) {
            System.out.println("Here should be nickname set");
        }
        String answer = "You wrote me: " + message.getText();
        System.out.println("Answer: " + answer);
        sendMessage(answer, message.getUserId(), false);
    }

    private void sendTestImage(String userId) {
        sendMessage("Нужно бы забраться повыше и осмотреться...", userId);
        String docName = "Test name";
        InputStream result = null;
        try {
            result = imageService.overlayImages(
                    imageService.getFile("images/locations/forest-test.png"),
                    imageService.getFile("images/items/weapons/swords/sword-test.png")
            );
        } catch (IOException e) {
            sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
        File file = null;
        try {
            file = imageService.inputStreamToFile(result, docName, ".png");
        } catch (IOException e) {
            sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
        if (file != null) {
//            sendDocument(file, userId);
            sendImage(file, userId);
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

        if (!message.getUserId().equals(WEBHOOK_ADMIN_ID)) {
            sendMessage(log, WEBHOOK_ADMIN_ID);
        }
    }

    private void performCommand(UpdateWrapper message) {
        String[] commandParts = message.getText().split(" ", 2);
        Command command;
        try {
            command = Command.valueOf(commandParts[0].substring(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            sendMessage(MainText.UNKNOWN_COMMAND.text(), message.getUserId(), true);
            return;
        }
        switch (command) {
            case ME:
                sendMessage(playerService.getPlayerInfo(message.getUserId()), message.getUserId(), true);
                break;
            case NICKNAME:
                if (commandParts.length > 1) {
                    setNickname(message.getPlayer(), commandParts[1]);
                } else {
                    sendMessage(MainText.EMPTY_NICKNAME.text(), message.getUserId(), true);
                }
                break;
            case ADD:
                String[] values = commandParts[1].split(" ", 2);
                try {
                    try {
                        ArrayList<String> eventList = message.getPlayer().addStatExp(
                                Integer.valueOf(values[1]),
                                Stats.valueOf(values[0].toUpperCase())
                        );
                        if (eventList != null && !eventList.isEmpty()) {
                            for (String event : eventList) {
                                if (event != null && !event.equals("")) {
                                    sendMessage(event, message.getUserId());
                                }
                            }
                        }
                        sendMessage("Очков опыта получено: " + values[1], message.getUserId());
                        playerService.update(message.getPlayer());
                    } catch (NumberFormatException e) {
                        sendMessageToAdmin("Не торопись, это слишком много");
                    }
                } catch (SOCInvalidDataException e) {
                    sendMessageToAdmin(e.getMessage());
                    e.printStackTrace();
                }
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
                    sendMessage(locationService.getAll().toString(), message.getUserId());
                }
                break;
            default:
                sendMessage(MainText.COMMAND_NOT_DEFINED.text(), message.getUserId(), true);
        }
    }

    private Player getPlayer(String externalId) {
        Player player;
        player = playerService.findByExternalId(externalId);
        if (player == null) {
            player = new Player(externalId, externalId);
            player = playerService.create(player);
        }
        return player;
    }

    private void setNickname(Player player, String newName) {
        player.setNickname(newName);
        playerService.update(player);
        sendMessage(MainText.NICKNAME_CHANGED.text() + player.getNickname() + "*", player.getExternalId(), true);
    }

    private Boolean alreadyRedirected;

    private void sendMessage(String text, String userId) {
        sendMessage(text, userId, false);
    }

    private void sendMessage(String text, String userId, Boolean useMarkdown) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(useMarkdown);
        sendMessage.setChatId(userId);
        sendMessage.setText(text);
        try {
            webHook.execute(sendMessage);
            alreadyRedirected = false;
        } catch (NullPointerException e) {
            System.out.println("Spring Startup Error (Autowired Services not initialized)");
            try {
                new WebHookHandler().execute(sendMessage);
            } catch (TelegramApiException ex) {
                sendMessageToAdmin(e.getMessage());
                ex.printStackTrace();
            }
        } catch (TelegramApiException e) {
            sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendImage(File file, String userId) {
        sendImage(file, userId, null);
    }
    private void sendImage(File file, String userId, String caption) {
        SendPhoto newMessage = new SendPhoto().setPhoto(file);
        if (caption != null && !caption.equals("")) {
            newMessage.setCaption(caption);
        }
        proceedImageSend(newMessage, userId);
    }
    private void sendImage(String fileName, InputStream stream, String userId) {
        sendImage(fileName, stream, userId, null);
    }
    private void sendImage(String fileName, InputStream stream, String userId, String caption) {
        SendPhoto newMessage = new SendPhoto().setPhoto(fileName, stream);
        if (caption != null && !caption.equals("")) {
            newMessage.setCaption(caption);
        }
        proceedImageSend(newMessage, userId);
    }
    private void proceedImageSend(SendPhoto newMessage, String userId) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;

        newMessage.setChatId(userId);
        try {
            webHook.execute(newMessage);
            alreadyRedirected = false;
        } catch (TelegramApiException e) {
            sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendDocument(File file, String userId) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;

        SendDocument sendMessage = new SendDocument().setDocument(file);
        sendMessage.setChatId(userId);
        try {
            webHook.execute(sendMessage);
            alreadyRedirected = false;
        } catch (TelegramApiException e) {
            sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessageToAdmin(String text) {
        sendMessage(text, BotConfig.WEBHOOK_ADMIN_ID, false);
    }

}
