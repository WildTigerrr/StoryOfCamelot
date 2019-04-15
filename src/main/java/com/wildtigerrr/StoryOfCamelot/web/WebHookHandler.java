package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.SOCBotConfig;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class WebHookHandler extends TelegramWebhookBot {

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        System.out.println("Whooo, I'm triggered");
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.getMessage());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessage.setText("You wrote me: " + update.getMessage().getText());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return sendMessage;
        }
        return null;
    }

    @Override
    public String getBotUsername() {
        return SOCBotConfig.WEBHOOK_USER;
    }

    @Override
    public String getBotToken() {
        return SOCBotConfig.WEBHOOK_TOKEN;
    }

    @Override
    public String getBotPath() {
        return SOCBotConfig.WEBHOOK_USER; //arbitrary path to deliver updates on, username is an example.
    }

}
