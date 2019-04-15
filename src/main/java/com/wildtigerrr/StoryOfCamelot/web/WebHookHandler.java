package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.SOCBotConfig;
import com.wildtigerrr.StoryOfCamelot.database.DatabaseInterraction;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.wildtigerrr.StoryOfCamelot.SOCBotConfig.mainAdminId;

public class WebHookHandler extends TelegramWebhookBot {

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update);
        }
        return null;
    }

    private void handleMessage(Update update) {
//        System.out.println(update.getMessage());
        logSender(update.getMessage().getFrom(), update.getMessage().getText());
        if (update.getMessage().getFrom().getId().toString().equals(mainAdminId)) {
            if (update.getMessage().getText().equals("/database create")) {
                DatabaseInterraction.createDatabase();
            } else if (update.getMessage().getText().equals("/database drop")) {
                DatabaseInterraction.dropDatabase();
            }
        }
        String answer = "You wrote me: " + update.getMessage().getText();
        System.out.println("Answer: " + answer);
        BotResponseHandler.sendMessage(answer, update.getMessage().getChatId().toString());
    }

    private void logSender(User user, String message) {
        String log = "New message, User:"
                + (user.getFirstName() == null ? "" : " " + user.getFirstName())
                + (user.getLastName() == null ? "" : " " + user.getLastName())
                + " (id" + user.getId().toString() + ")"
                + (user.getUserName() == null ? "" : ", also known as " + user.getUserName())
                + ", wrote a message: " + message;
        System.out.println(log);

        if (!user.getId().toString().equals(mainAdminId)) {
            BotResponseHandler.sendMessage(log, mainAdminId);
        }
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
        return SOCBotConfig.WEBHOOK_ADMIN; //arbitrary path to deliver updates on
    }

}
