package com.wildtigerrr.StoryOfCamelot.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class WebHookHandler extends TelegramWebhookBot {

    @Autowired
    private ResponseHandler responseHandler;

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            responseHandler.handleMessage(new UpdateWrapper(update, false));
        } else if (update.hasCallbackQuery()) {
            responseHandler.handleMessage(new UpdateWrapper(update, true));
        } else {
            System.out.println(update);
        }
        return null;
    }

    @Override
    public String getBotUsername() {
        return BotConfig.WEBHOOK_USER;
    }

    @Override
    public String getBotToken() {
        return BotConfig.WEBHOOK_TOKEN;
    }

    @Override
    public String getBotPath() {
        return BotConfig.WEBHOOK_ADMIN;
    }

}