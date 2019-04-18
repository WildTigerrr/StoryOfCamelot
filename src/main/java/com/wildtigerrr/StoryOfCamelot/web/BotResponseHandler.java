package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.SOCBotConfig;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class BotResponseHandler  {

    public void sendMessage(String text, String userId) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(text);
        try {
            new WebHookHandler().execute(sendMessage);
            alreadyRedirected = false;
        } catch (TelegramApiException e) {
            sendMessageWithoutLoop(e.getMessage());
        }
    }

    public void sendMessageToAdmin(String text) {
        sendMessage(text, SOCBotConfig.mainAdminId);
    }

    private Boolean alreadyRedirected;
    private void sendMessageWithoutLoop(String text) {
        sendMessageToAdmin(text);
    }

}
