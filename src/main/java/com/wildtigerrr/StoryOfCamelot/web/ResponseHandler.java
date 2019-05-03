package com.wildtigerrr.StoryOfCamelot.web;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.vdurmont.emoji.EmojiParser;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.FileProcessing;
import com.wildtigerrr.StoryOfCamelot.bin.enums.GameSettings;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.exceptions.SOCInvalidDataException;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.LocationNear;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.*;
import com.wildtigerrr.StoryOfCamelot.web.service.AmazonClient;
import com.wildtigerrr.StoryOfCamelot.web.service.ScheduledAction;
import com.wildtigerrr.StoryOfCamelot.web.service.TimeDependentActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.wildtigerrr.StoryOfCamelot.web.BotConfig.WEBHOOK_ADMIN_ID;

@Service
public class ResponseHandler {

    @Autowired private WebHookHandler webHook;
    @Autowired private PlayerServiceImpl playerService;
    @Autowired private FileLinkServiceImpl fileLinkService;
    @Autowired private LocationServiceImpl locationService;
    @Autowired private LocationNearServiceImpl locationNearService;
    @Autowired private FileProcessing imageService;
    private AmazonClient amazonClient;

    public ResponseHandler() {}

    @SuppressWarnings("unused")
    @Autowired
    ResponseHandler(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    void handleMessage(UpdateWrapper message) {
        message.setPlayer(getPlayer(message.getUserId()));
        System.out.println("Working with message: " + message);
        logSender(message);
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
                Player player = addExperience(message.getPlayer(), Stats.valueOf(values[0].toUpperCase()), Integer.parseInt(values[1]), true);
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
                    sendAvailableLocations(message.getPlayer());
                } else if (message.isQuery()) {
                    moveToLocation(message, commandParts[1]);
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
            player = new Player(externalId, externalId, locationService.findByName(GameSettings.DEFAULT_LOCATION.get()));
            player = playerService.create(player);
        }
        return player;
    }

    private void setNickname(Player player, String newName) {
        player.setNickname(newName);
        playerService.update(player);
        sendMessage(MainText.NICKNAME_CHANGED.text() + player.getNickname() + "*", player.getExternalId(), true);
    }

    private Player addExperience(Player player, Stats stat, int experience, Boolean sendExperienceGet) {
        try {
            ArrayList<String> eventList = player.addStatExp(
                    experience,
                    stat
            );
            if (eventList != null && !eventList.isEmpty()) {
                for (String event : eventList) {
                    if (event != null && !event.equals("")) {
                        sendMessage(event, player.getExternalId());
                    }
                }
            }
            if (sendExperienceGet) {
                sendMessage("Очков опыта получено: " + experience, player.getExternalId());
            }
            return player;
        } catch (SOCInvalidDataException e) {
            sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
        return player;
    }

    private void sendAvailableLocations(Player player) {
        ArrayList<Location> nearLocations = locationNearService.getNearLocations(player.getLocation());
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button;
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        Integer buttonsCounter = 0;
        for (Location loc : nearLocations) {
            buttonsCounter++;
            if (buttonsCounter > 2) {
                rowList.add(buttonsRow);
                buttonsRow = new ArrayList<>();
                buttonsCounter = 1;
            }
            button = new InlineKeyboardButton();
            button.setText(loc.getName());
            button.setCallbackData("/move " + loc.getId());
            buttonsRow.add(button);
        }
        rowList.add(buttonsRow);
        keyboard.setKeyboard(rowList);
        SendMessage message = new SendMessage();
        message.setText("Итак, куда пойдём?");
        message.setChatId(player.getExternalId());
        message.setReplyMarkup(keyboard);
        message.enableMarkdown(true);
        sendMessage(message);
    }

    private void moveToLocation(UpdateWrapper message, String locationId) {
        Location location = locationService.findById(Integer.parseInt(locationId));
        if (location != null) {
            int distance = locationNearService.getDistance(message.getPlayer().getLocation(), location);
            if (distance == -1) {
                sendMessage(MainText.NO_DIRECT.text(), message.getUserId());
                return;
            }
            EditMessageText messageEdit = new EditMessageText();
            messageEdit.setMessageId(message.getMessageId());
            messageEdit.setChatId(message.getUserId());
            messageEdit.setText("Ну, пойдем к " + location.getName());
            messageEdit.enableMarkdown(true);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, distance);
            Boolean scheduled = TimeDependentActions.scheduleMove(message.getPlayer().getId(), calendar.getTimeInMillis(), locationId, String.valueOf(distance));
            if (!scheduled) {
                messageEdit.setText(MainText.ALREADY_MOVING.text());
            }
            try {
                webHook.execute(messageEdit);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendLocationUpdate(ScheduledAction action) {
        Player player = playerService.findById(action.playerId);
        Location location = locationService.findById(Integer.valueOf(action.target));
        player.setLocation(location);
        addExperience(player, Stats.ENDURANCE, Integer.valueOf(action.additionalValue) / 10, true);
        playerService.update(player);
        if (location.getImageLink() != null) {
            InputStream stream = amazonClient.getObject(location.getImageLink().getLocation());
            sendImage(location.getName(), stream, player.getExternalId(), location.getName() + ", и что у нас тут?");
        } else {
            sendMessage(location.getName() + ", и что у нас тут?", player.getExternalId());
        }
    }

    private Boolean alreadyRedirected;

    public void sendMessage(String text, String userId) {
        sendMessage(text, userId, false);
    }

    public void sendMessage(String text, String userId, Boolean useMarkdown) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(useMarkdown);
        message.setChatId(userId);
        message.setText(text);
        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;
        try {
            webHook.execute(message);
            alreadyRedirected = false;
        } catch (NullPointerException e) {
            System.out.println("Spring Startup Error (Autowired Services not initialized)");
            try {
                new WebHookHandler().execute(message);
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
