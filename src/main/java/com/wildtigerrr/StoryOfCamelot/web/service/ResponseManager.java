package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.WebHookHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
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

    private static final Logger log = LogManager.getLogger(ResponseManager.class);

    @Autowired
    private WebHookHandler webHook;
    private Boolean alreadyRedirected = false;

    public void sendErrorReport(Exception e) {
        log.error(e.getMessage(), e);
        postMessageToAdminChannel(e.getMessage());
    }

    public static void sendErrorReport(String message, Exception e, Boolean applyMarkup) {
        log.error(message, e);
        postMessageToAdminChannelNonWired(e.getMessage(), applyMarkup);
    }

    public void sendMessageToAdmin(String text) {
        proceedMessageSend(text, null, BotConfig.WEBHOOK_ADMIN_ID, false);
    }

    public static void postMessageToAdminChannelNonWired(String text) {
        new ResponseManager().proceedMessageSend(text, null, BotConfig.ADMIN_CHANNEL_ID, false);
    }

    public static void postMessageToAdminChannelNonWired(String text, Boolean applyMarkup) {
        new ResponseManager().proceedMessageSend(text, null, BotConfig.ADMIN_CHANNEL_ID, applyMarkup);
    }

    public void postMessageToAdminChannel(String text) {
        proceedMessageSend(text, null, BotConfig.ADMIN_CHANNEL_ID, false);
    }

    public void postMessageToAdminChannel(String text, Boolean applyMarkup) {
        proceedMessageSend(text, null, BotConfig.ADMIN_CHANNEL_ID, applyMarkup);
    }

    public void sendMessage(String text, String userId) {
        proceedMessageSend(text, null, userId, false);
    }

    public void sendMessage(String text, String userId, Boolean useMarkdown) {
        proceedMessageSend(text, null, userId, useMarkdown);
    }

    public void sendMessage(String text, ReplyKeyboard keyboard, String userId) {
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
        proceedMessageEdit(messageId, null, newText, userId, useMarkdown);
    }

    public void sendMessageEdit(Integer messageId, String newText, InlineKeyboardMarkup keyboard, String userId, Boolean useMarkdown) {
        proceedMessageEdit(messageId, keyboard, newText, userId, useMarkdown);
    }

    public void sendCallbackAnswer(String queryId, String text, Boolean isAlert) {
        proceedAnswerCallback(queryId, text, isAlert);
    }

    public void sendCallbackAnswer(String queryId, String text) {
        proceedAnswerCallback(queryId, text, false);
    }

    public void sendCallbackAnswer(String queryId) {
        proceedAnswerCallback(queryId, null, false);
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

    private void execute(PartialBotApiMethod method) {
        try {
            webHook.execute(method);
        } catch (NullPointerException e) {
            executeBeforeAutowiring(method);
        } catch (TelegramApiException e) {
            handleError(e);
        }
    }

    private void executeBeforeAutowiring(PartialBotApiMethod method) {
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
        sendMessageToAdmin(e.getMessage());
    }

    private Boolean isRedirected() {
        if (!alreadyRedirected) {
            alreadyRedirected = true;
            return false;
        }
        return true;
    }

}
