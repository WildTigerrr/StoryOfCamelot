package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.SOCBotConfig;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class WebHookHandler extends TelegramWebhookBot {

    Integer messagesToMe;
    Integer messagesToNastya;

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        System.out.println("Whooo, I'm triggered");
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.getMessage());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessage.setText("You wrote me: " + update.getMessage().getText());
            if (update.getMessage().getFrom().getUserName().equals("wildtigerrr")) {
                if (messagesToMe == null) {
                    messagesToMe = 1;
                    sendMessage.setText("RrrrRrrrRr");
                } else {
                    messagesToMe++;
                    sendMessage.setText("You wrote me: " + update.getMessage().getText() + ", that's my " + messagesToMe + " message to you.");
                }
            }
            if (update.getMessage().getFrom().getUserName().equals("nastassja_t")) {
                if (messagesToNastya == null) {
                    messagesToNastya = 1;
                    sendMessage.setText("Привет, Солнышко =*");
                } else {
                    messagesToNastya++;
                    sendMessage.setText("You wrote me: " + update.getMessage().getText() + ", that's my " + messagesToNastya + " message to you.");
                }
            }
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
