package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;

@Log4j2
@Service
public class TelegramWebHookHandler extends TelegramWebhookBot {

    private ResponseManager messages;
    private UpdateReceiver receiver;

    @Autowired
    public TelegramWebHookHandler(ResponseManager messages, UpdateReceiver receiver) {
        this.messages = messages;
        this.receiver = receiver;
    }

    public TelegramWebHookHandler() {}

    @PostConstruct
    public void init() {
        this.messages.setExecutor(this);
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        receiver.process(update);
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