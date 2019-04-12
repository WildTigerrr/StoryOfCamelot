package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.SOCBotConfig;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class WebHookHandler extends TelegramWebhookBot {
    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessage.setText("Well, all information looks like noise until you break the code.");
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
