package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.web.TelegramWebHookHandler;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("responseManager")
@Profile("test")
public class MockResponseManager implements ResponseManager {

    @Override
    public void run() {

    }

    @Override
    public void setExecutor(TelegramWebHookHandler telegramWebHookHandler) {

    }

    @Override
    public void sendMessage(ResponseMessage message) {

    }

    @Override
    public void sendErrorReport(String message, Exception e, Boolean applyMarkup) {

    }

    @Override
    public void sendErrorReport(String message, Exception e) {

    }

    @Override
    public void sendErrorReport(Exception e) {

    }

    @Override
    public void postMessageToAdminChannel(String text, Boolean applyMarkup) {

    }

    @Override
    public void postMessageToAdminChannel(String text) {

    }

    @Override
    public void sendAnswer(String queryId, String text, Boolean isAlert) {

    }

    @Override
    public void sendAnswer(String queryId, String text) {

    }

    @Override
    public void sendAnswer(String queryId) {

    }
}
