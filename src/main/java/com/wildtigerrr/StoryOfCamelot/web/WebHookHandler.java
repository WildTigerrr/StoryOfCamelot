package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j2
@Service
public class WebHookHandler extends TelegramWebhookBot {

    @Autowired
    private ResponseHandler responseHandler;
    @Autowired
    private ResponseManager messages;

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            responseHandler.handleMessage(new UpdateWrapper(update, false));
        } else if (update.hasCallbackQuery()) {
            responseHandler.handleMessage(new UpdateWrapper(update, true));
        } else {
            log.error("Message not supported: " + update.toString());
            messages.postMessageToAdminChannel("Message not supported: " + update.toString());
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