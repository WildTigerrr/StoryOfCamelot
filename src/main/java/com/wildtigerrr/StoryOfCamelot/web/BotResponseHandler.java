package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.SOCBotConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotResponseHandler  {

    public static void sendMessage(String text, String userId) {
        if (alreadyRedirected == null) alreadyRedirected = true;
        else return;

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(text);
        try {
            new WebHookHandler().execute(sendMessage);
        } catch (TelegramApiException e) {
            sendMessageWithoutLoop(e.getMessage());
        }
    }

    public static void sendMessageToAdmin(String text) {
        sendMessage(text, SOCBotConfig.mainAdminId);
    }

    private static Boolean alreadyRedirected;
    private static void sendMessageWithoutLoop(String text) {
        sendMessageToAdmin(text);
    }

}
