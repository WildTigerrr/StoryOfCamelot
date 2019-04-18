package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.SOCBotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class BotResponseHandler  {

    @Autowired
    private WebHookHandler webHook;

    public void handleMessage() {

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
            System.out.println("WebHook Startup Error");
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
