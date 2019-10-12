package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.WebHookHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.InputStream;

@Log4j2
@Service
public class ResponseManager {

    private WebHookHandler webHook;
    private Boolean alreadyRedirected = false;

    public void setWebHook(WebHookHandler webHookHandler) {
        this.webHook = webHookHandler;
    }

    public void sendErrorReport(String message, Exception e, Boolean applyMarkup) {
        log.error(message, e);
        postMessageToAdminChannel(e.getMessage(), applyMarkup);
    }
    public void sendErrorReport(String message, Exception e) {
        sendErrorReport(message, e, false);
    }
    public void sendErrorReport(Exception e) {
        sendErrorReport(e.getMessage(), e, false);
    }


    public static void postMessageToAdminChannelOnStart(String text, Boolean applyMarkup) {
        new ResponseManager().proceedMessageSend(text, null, BotConfig.ADMIN_CHANNEL_ID, applyMarkup);
    }
    public static void postMessageToAdminChannelOnStart(String text) {
        postMessageToAdminChannelOnStart(text, false);
    }


    public void postMessageToAdminChannel(String text, Boolean applyMarkup) {
        proceedMessageSend(text, null, BotConfig.ADMIN_CHANNEL_ID, applyMarkup);
    }
    public void postMessageToAdminChannel(String text) {
        postMessageToAdminChannel(text, false);
    }


    public void sendMessage(String text, ReplyKeyboard keyboard, String userId) {
        proceedMessageSend(text, keyboard, userId, true);
    }
    public void sendMessage(String text, String userId, Boolean useMarkdown) {
        proceedMessageSend(text, null, userId, useMarkdown);
    }
    public void sendMessage(String text, String userId) {
        sendMessage(text, userId, false);
    }


    public void sendImage(String fileName, InputStream stream, String userId, String caption) {
        proceedImageSend(null, fileName, stream, userId, caption);
    }
    public void sendImage(String fileName, InputStream stream, String userId) {
        sendImage(fileName, stream, userId, null);
    }


    public void sendImage(File file, String userId, String caption) {
        proceedImageSend(file, null, null, userId, caption);
    }
    public void sendImage(File file, String userId) {
        if (file == null || userId == null) return;
        sendImage(file, userId, null);
    }


    public void sendDocument(File file, String userId) {
        proceedDocumentSend(file, userId);
    }


    public void sendMessageEdit(Integer messageId, String newText, InlineKeyboardMarkup keyboard, String userId, Boolean useMarkdown) {
        proceedMessageEdit(messageId, keyboard, newText, userId, useMarkdown);
    }
    public void sendMessageEdit(Integer messageId, String newText, String userId, Boolean useMarkdown) {
        sendMessageEdit(messageId, newText, null, userId, useMarkdown);
    }


    public void sendCallbackAnswer(String queryId, String text, Boolean isAlert) {
        proceedAnswerCallback(queryId, text, isAlert);
    }
    public void sendCallbackAnswer(String queryId, String text) {
        sendCallbackAnswer(queryId, text, false);
    }
    public void sendCallbackAnswer(String queryId) {
        sendCallbackAnswer(queryId, null, false);
    }


    private void proceedMessageSend(String text, ReplyKeyboard keyboard, String userId, Boolean useMarkdown) {
        SendMessage message = new SendMessage()
                .enableMarkdown(useMarkdown)
                .setChatId(userId)
                .setText(text)
                .setReplyMarkup(keyboard);
        execute(message);
    }
    private void proceedImageSend(File file, String fileName, InputStream stream, String userId, String caption) {
        SendPhoto newMessage = new SendPhoto()
                .setCaption(caption)
                .setChatId(userId);
        if (file != null) {
            newMessage.setPhoto(file);
        } else if (stream != null) {
            newMessage.setPhoto(fileName, stream);
        }
        execute(newMessage);
    }
    private void proceedDocumentSend(File file, String userId) {
        SendDocument sendMessage = new SendDocument()
                .setDocument(file)
                .setChatId(userId);
        execute(sendMessage);
    }
    private void proceedMessageEdit(Integer messageId, InlineKeyboardMarkup keyboard, String newText, String userId, Boolean useMarkdown) {
        EditMessageText messageEdit = new EditMessageText()
                .setMessageId(messageId)
                .setChatId(userId)
                .setText(newText)
                .enableMarkdown(useMarkdown)
                .setReplyMarkup(keyboard);
        execute(messageEdit);
    }
    private void proceedAnswerCallback(String queryId, String text,Boolean isAlert) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                .setCallbackQueryId(queryId)
                .setShowAlert(isAlert)
                .setText(text);
        execute(answerCallbackQuery);
    }


    private void execute(BotApiMethod method) {
        try {
            webHook.execute(method);
        } catch (NullPointerException e) {
            executeBeforeAutowiring(method);
        } catch (TelegramApiException e) {
            handleError(e);
        }
    }
    private void execute(SendPhoto method) {
        try {
            webHook.execute(method);
        } catch (TelegramApiException e) {
            handleError(e);
        }
    }
    private void execute(SendDocument method) {
        try {
            webHook.execute(method);
        } catch (TelegramApiException e) {
            handleError(e);
        }
    }

    private void executeBeforeAutowiring(BotApiMethod method) {
        log.warn("Spring Startup Error (Autowired Services not initialized)");
        try {
            new WebHookHandler().execute(method);
        } catch (TelegramApiException ex) {
            handleError(ex);
        }
    }

    private void handleError(TelegramApiException e) {
        log.error("Exception On Sending Message", e);
        log.error("Attempt to retry: " + !alreadyRedirected);
        if (isRedirected()) return;
        postMessageToAdminChannel(e.getMessage());
    }

    private Boolean isRedirected() {
        if (!alreadyRedirected) {
            alreadyRedirected = true;
            return false;
        }
        return true;
    }

}
