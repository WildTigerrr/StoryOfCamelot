package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.web.WebHookHandler;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.io.File;
import java.io.InputStream;

@Service
@Profile("test")
public class MockResponseManager implements ResponseManager {

    @Override
    public void setExecutor(WebHookHandler webHookHandler) {

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
    public void sendMessage(String text, ReplyKeyboard keyboard, String userId) {

    }

    @Override
    public void sendMessage(String text, String userId, Boolean useMarkdown) {

    }

    @Override
    public void sendMessage(String text, String userId) {

    }

    @Override
    public void sendImage(String fileName, InputStream stream, String userId, String caption) {

    }

    @Override
    public void sendImage(String fileName, InputStream stream, String userId) {

    }

    @Override
    public void sendImage(File file, String userId, String caption) {

    }

    @Override
    public void sendImage(File file, String userId) {

    }

    @Override
    public void sendImage(String fileId, String userId, String caption) {

    }

    @Override
    public void sendImage(String fileId, String userId) {

    }

    @Override
    public void sendDocument(File file, String userId) {

    }

    @Override
    public void sendMessageEdit(Integer messageId, String newText, InlineKeyboardMarkup keyboard, String userId, Boolean useMarkdown) {

    }

    @Override
    public void sendMessageEdit(Integer messageId, String newText, String userId, Boolean useMarkdown) {

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
