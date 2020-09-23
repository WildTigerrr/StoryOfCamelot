package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.web.TelegramWebHookHandler;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;

public interface ResponseManager extends Runnable {

    void setExecutor(TelegramWebHookHandler telegramWebHookHandler);

    void sendMessage(ResponseMessage message);

    void sendErrorReport(String message, Exception e, Boolean applyMarkup);
    void sendErrorReport(String message, Exception e);
    void sendErrorReport(Exception e);

    void postMessageToAdminChannel(String text, Boolean applyMarkup);
    void postMessageToAdminChannel(String text);

    void sendAnswer(String queryId, String text, Boolean isAlert);
    void sendAnswer(String queryId, String text);
    void sendAnswer(String queryId);



}
