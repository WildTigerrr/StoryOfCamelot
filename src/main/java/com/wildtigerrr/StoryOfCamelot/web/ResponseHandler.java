package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.Command;
import com.wildtigerrr.StoryOfCamelot.bin.MainText;
import com.wildtigerrr.StoryOfCamelot.database.DatabaseInteraction;
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
    private DatabaseInteraction dbService;

    @Autowired
    private PlayerServiceImpl playerDao;

    public void handleMessage(UpdateWrapper message) {
        logSender(message);
        message.setPlayer(getPlayer(message.getUserId()));
        Player player = message.getPlayer();
        if (message.getText().startsWith("/")) {
            String commandParts[] = message.getText().split(" ", 2);
            Command command;
            try {
                command = Command.valueOf(commandParts[0].substring(1).toUpperCase());
            } catch (IllegalArgumentException e) {
                sendMessage(MainText.UNKNOWN_COMMAND.text(), message.getUserId());
                return;
            }
            switch (command) {
                case ME: sendMessage(dbService.testGetPlayer(message.getUserId()), message.getUserId()); break;
                case NICKNAME: setNickname(player, commandParts[1]); break;
                default: sendMessage("Слушай, я о чем-то таком слышал, но почему-то не знаю что делать", message.getUserId());
            }
        }
        if (message.getPlayer().isNew()) {
            player.setup();
            sendMessage(MainText.MEET_NEW_PLAYER.text(), message.getUserId());
        } else if (player.getExternalId().equals(player.getNickname())) {

        }
        if (message.getUserId().equals(WEBHOOK_ADMIN_ID)) {
            if (message.getText().equals("/database test")) {
                dbService.testSavePlayer(message.getUserId());
            }
        }
        String answer = "You wrote me: " + message.getText();
        System.out.println("Answer: " + answer);
        sendMessage(answer, message.getUserId());
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

    private Player getPlayer(String externalId) {
        Player player = null;
        player = playerDao.findByExternalId(externalId);
        if (player == null) {
            player = new Player(externalId, externalId);
            player = playerDao.create(player);
        }
        return player;
    }

    private void setNickname(Player player, String newName) {
        player.setNickname(newName);
        playerDao.update(player);
        sendMessage("Вы смогли переписать историю. Теперей вас будут помнить как " + newName, player.getExternalId());
    }

    private Boolean alreadyRedirected;
    public void sendMessage(String text, String userId) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
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
        }
    }

    public void sendMessageToAdmin(String text) {
        sendMessage(text, BotConfig.WEBHOOK_ADMIN_ID);
    }

}
