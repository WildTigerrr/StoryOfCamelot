package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.WebHookHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.InputStream;

@Service
public class ResponseManager {

    @Autowired
    private WebHookHandler webHook;
    private Boolean alreadyRedirected;

    public void sendMessageToAdmin(String text) {
        proceedMessageSend(text, null, BotConfig.WEBHOOK_ADMIN_ID, false);
    }

    public void sendMessage(String text, String userId) {
        proceedMessageSend(text, null, userId, false);
    }

    public void sendMessage(String text, String userId, Boolean useMarkdown) {
        proceedMessageSend(text, null, userId, useMarkdown);
    }

    public void sendMessage(String text, InlineKeyboardMarkup keyboard, String userId) {
        proceedMessageSend(text, keyboard, userId, true);
    }

    public void sendImage(File file, String userId) {
        proceedImageSend(file, null, null, userId, null);
    }

    public void sendImage(File file, String userId, String caption) {
        proceedImageSend(file, null, null, userId, caption);
    }

    public void sendImage(String fileName, InputStream stream, String userId) {
        proceedImageSend(null, fileName, stream, userId, null);
    }

    public void sendImage(String fileName, InputStream stream, String userId, String caption) {
        proceedImageSend(null, fileName, stream, userId, caption);
    }

    public void sendDocument(File file, String userId) {
        proceedDocumentSend(file, userId);
    }

    public void sendMessageEdit(Integer messageId, String newText, String userId, Boolean useMarkdown) {
        proceedMessageEdit(messageId, newText, userId, useMarkdown);
    }

    private void proceedMessageSend(String text, ReplyKeyboard keyboard, String userId, Boolean useMarkdown) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;

        SendMessage message = new SendMessage();
        message.enableMarkdown(useMarkdown);
        message.setChatId(userId);
        message.setText(text);
        if (keyboard != null)
            message.setReplyMarkup(keyboard);
        try {
            webHook.execute(message);
            alreadyRedirected = false;
        } catch (NullPointerException e) {
            System.out.println("Spring Startup Error (Autowired Services not initialized)");
            try {
                new WebHookHandler().execute(message);
            } catch (TelegramApiException ex) {
                sendMessageToAdmin(e.getMessage());
                ex.printStackTrace();
            }
        } catch (TelegramApiException e) {
            sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
    }

    private void proceedImageSend(File file, String fileName, InputStream stream, String userId, String caption) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;

        SendPhoto newMessage = new SendPhoto();
        if (file != null) {
            newMessage.setPhoto(file);
        } else if (stream != null) {
            newMessage.setPhoto(fileName, stream);
        }
        if (caption != null && !caption.equals("")) {
            newMessage.setCaption(caption);
        }
        newMessage.setChatId(userId);

        try {
            webHook.execute(newMessage);
            alreadyRedirected = false;
        } catch (TelegramApiException e) {
            sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
    }

    private void proceedDocumentSend(File file, String userId) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;

        SendDocument sendMessage = new SendDocument().setDocument(file);
        sendMessage.setChatId(userId);
        try {
            webHook.execute(sendMessage);
            alreadyRedirected = false;
        } catch (TelegramApiException e) {
            sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
    }

    private void proceedMessageEdit(Integer messageId, String newText, String userId, Boolean useMarkdown) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;

        EditMessageText messageEdit = new EditMessageText();
        messageEdit.setMessageId(messageId);
        messageEdit.setChatId(userId);
        messageEdit.setText(newText);
        messageEdit.enableMarkdown(useMarkdown);

        try {
            webHook.execute(messageEdit);
            alreadyRedirected = false;
        } catch (TelegramApiException e) {
            sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
    }

}
