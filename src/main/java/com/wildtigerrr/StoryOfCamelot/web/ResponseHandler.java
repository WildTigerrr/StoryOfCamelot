package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.MainText;
import com.wildtigerrr.StoryOfCamelot.database.DatabaseInteraction;
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

    public void handleMessage(UpdateWrapper message) {
        logSender(message);
        if (message.getPlayer() != null && message.getPlayer().isNew()) {
            sendMessage(MainText.MEET_NEW_PLAYER.text(), message.getUserId());
        }
        if (message.getUserId().equals(WEBHOOK_ADMIN_ID)) {
            if (message.getText().equals("/database test")) {
                dbService.testSavePlayer(message.getUserId());
            } else if (message.getText().equals("/me")) {
                sendMessage(dbService.testGetPlayer(message.getUserId()), message.getUserId());
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

    public void sendMessage(String text, String userId) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;

        SendMessage sendMessage = new SendMessage();
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
                sendMessageWithoutLoop(e.getMessage());
                ex.printStackTrace();
            }
        } catch (TelegramApiException e) {
            sendMessageWithoutLoop(e.getMessage());
        }
    }

    public void sendMessageToAdmin(String text) {
        sendMessage(text, BotConfig.WEBHOOK_ADMIN_ID);
    }

    private Boolean alreadyRedirected;
    private void sendMessageWithoutLoop(String text) {
        sendMessageToAdmin(text);
    }

}
