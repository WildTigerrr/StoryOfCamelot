package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.Command;
import com.wildtigerrr.StoryOfCamelot.bin.MainText;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.wildtigerrr.StoryOfCamelot.web.BotConfig.WEBHOOK_ADMIN_ID;

@Service
public class ResponseHandler {

    @Autowired
    private WebHookHandler webHook;

    @Autowired
    private PlayerServiceImpl playerDao;

    public void handleMessage(UpdateWrapper message) {
        System.out.println("Working with message: " + message);
        logSender(message);
        message.setPlayer(getPlayer(message.getUserId()));
        if (message.getText().startsWith("/")) {
            performCommand(message);
            return;
        }
        Player player = message.getPlayer();
        if (message.getPlayer().isNew()) {
            System.out.println("New player");
            player.setup();
            playerDao.update(player);
            sendMessage(MainText.MEET_NEW_PLAYER.text(), message.getUserId(), true);
        } else if (player.getExternalId().equals(player.getNickname())) {
            System.out.println("Here should be nickname set");
        }
        if (message.getUserId().equals(WEBHOOK_ADMIN_ID)) {
            if (message.getText().equals("/database test")) {
                // Some admin actions
            }
        }
        String answer = "You wrote me: " + message.getText();
        System.out.println("Answer: " + answer);
        sendMessage(answer, message.getUserId(), false);
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
            sendMessage(log, WEBHOOK_ADMIN_ID, false);
        }
    }

    private void performCommand(UpdateWrapper message) {
        String commandParts[] = message.getText().split(" ", 2);
        Command command;
        try {
            command = Command.valueOf(commandParts[0].substring(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            sendMessage(MainText.UNKNOWN_COMMAND.text(), message.getUserId(), true);
            return;
        }
        switch (command) {
            case ME:
                sendMessage(playerDao.getPlayerInfo(message.getUserId()), message.getUserId(), true);
                break;
            case NICKNAME:
                if (commandParts.length > 1) {
                    setNickname(message.getPlayer(), commandParts[1]);
                } else {
                    sendMessage(MainText.EMPTY_NICKNAME.text(), message.getUserId(), true);
                }
                break;
            default:
                sendMessage(MainText.COMMAND_NOT_DEFINED.text(), message.getUserId(), true);
        }
    }

    private Player getPlayer(String externalId) {
        Player player;
        player = playerDao.findByExternalId(externalId);
        if (player == null) {
            player = new Player(externalId, externalId);
            player = playerDao.create(player);
        }
        return player;
    }

    private void setNickname(Player player, String newName) {
        System.out.println("New nickname would be: " + newName);
        player.setNickname(newName);
        playerDao.update(player);
        sendMessage(MainText.NICKNAME_CHANGED.text() + player.getNickname() + "*", player.getExternalId(), true);
    }

    private Boolean alreadyRedirected;

    public void sendMessage(String text, String userId, Boolean useMarkdown) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;
        System.out.println("Message text: " + text);

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
            e.printStackTrace();
            sendMessageToAdmin(e.getMessage());
        }
    }

    public void sendMessageToAdmin(String text) {
        sendMessage(text, BotConfig.WEBHOOK_ADMIN_ID, false);
    }

}
