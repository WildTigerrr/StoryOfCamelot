package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.SOCBotConfig;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class WebHookHandler extends TelegramWebhookBot {

    Integer messagesToMe;
    Integer messagesToNastya;

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update);
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

    private void handleMessage(Update update) {
//        System.out.println(update.getMessage());
        logSender(update.getMessage().getFrom(), update.getMessage().getText());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText("You wrote me: " + update.getMessage().getText());

        System.out.println("Answer: " + sendMessage.getText());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void logSender(User user, String message) {
        String log = "New message, User: " + user.getFirstName() + " " + user.getLastName();
        if (user.getUserName() != null) {
            log = log + ", also known as " + user.getUserName();
        }
        log = log + ", wrote a message: " + message;
        System.out.println(log);

        if (!user.getId().toString().equals("413316947")) {
            SendMessage msg = new SendMessage();
            msg.setChatId("413316947");
            msg.setText(log);
            try {
                execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
