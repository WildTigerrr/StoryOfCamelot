package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.SOCBotConfig;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.wildtigerrr.StoryOfCamelot.SOCBotConfig.mainAdminId;

public class WebHookHandler extends TelegramWebhookBot {

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
        String log = "New message, User:"
                + (user.getFirstName() == null ? "" : " " + user.getFirstName())
                + (user.getLastName() == null ? "" : " " + user.getLastName())
                + " (id" + user.getId().toString() + ")"
                + (user.getUserName() == null ? "" : ", also known as" + user.getUserName())
                + ", wrote a message: " + message;
        System.out.println(log);

        if (!user.getId().toString().equals(mainAdminId)) {
            SendMessage msg = new SendMessage();
            msg.setChatId(mainAdminId);
            msg.setText(log);
            try {
                execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
