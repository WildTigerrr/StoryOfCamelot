package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.web.WebHookHandler;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.io.File;
import java.io.InputStream;

public interface ResponseManager {

    void setExecutor(WebHookHandler webHookHandler);

    void sendMessage(ResponseMessage message);

    void sendErrorReport(String message, Exception e, Boolean applyMarkup);
    void sendErrorReport(String message, Exception e);
    void sendErrorReport(Exception e);

    void postMessageToAdminChannel(String text, Boolean applyMarkup);
    void postMessageToAdminChannel(String text);

    void sendMessage(String text, ReplyKeyboard keyboard, String userId);
    void sendMessage(String text, String userId, Boolean useMarkdown);
    void sendMessage(String text, String userId);

    void sendImage(String fileName, InputStream stream, String userId, String caption);
    void sendImage(String fileName, InputStream stream, String userId);
    void sendImage(File file, String userId, String caption);
    void sendImage(File file, String userId);
    void sendImage(String fileId, String userId, String caption);
    void sendImage(String fileId, String userId);

    void sendDocument(File file, String userId);

    void sendMessageEdit(Integer messageId, String newText, InlineKeyboardMarkup keyboard, String userId, Boolean useMarkdown);
    void sendMessageEdit(Integer messageId, String newText, String userId, Boolean useMarkdown);

    void sendAnswer(String queryId, String text, Boolean isAlert);
    void sendAnswer(String queryId, String text);
    void sendAnswer(String queryId);



}
