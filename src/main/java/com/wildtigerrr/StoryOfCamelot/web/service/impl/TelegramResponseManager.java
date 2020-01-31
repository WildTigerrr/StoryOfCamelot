package com.wildtigerrr.StoryOfCamelot.web.service.impl;

import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.WebHookHandler;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.InputStream;

@Log4j2
@Service
@Profile("!test")
public class TelegramResponseManager implements ResponseManager {

    private WebHookHandler webHook;
    private Boolean alreadyRedirected = false;

    public void setExecutor(WebHookHandler webHookHandler) {
        this.webHook = webHookHandler;
    }

    public void sendMessage(ResponseMessage message) {
        switch (message.getType()) {
            case TEXT: proceedMessageSend((TextResponseMessage) message); break;
            case PHOTO: proceedImageSend((ImageResponseMessage) message); break;
            case DOCUMENT: proceedDocumentSend((DocumentResponseMessage) message); break;
            case STICKER: proceedStickerSend((StickerResponseMessage) message); break;
        }
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
        proceedImageSend(null, fileName, stream, null, userId, caption);
    }
    public void sendImage(String fileName, InputStream stream, String userId) {
        sendImage(fileName, stream, userId, null);
    }


    public void sendImage(File file, String userId, String caption) {
        proceedImageSend(file, null, null, null, userId, caption);
    }
    public void sendImage(File file, String userId) {
        if (file == null || userId == null) return;
        sendImage(file, userId, null);
    }

    public void sendImage(String fileId, String userId, String caption) {
        proceedImageSend(null, null, null, fileId, userId, caption);
    }
    public void sendImage(String fileId, String userId) {
        sendImage(fileId, userId, null);
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


    public void sendAnswer(String queryId, String text, Boolean isAlert) {
        proceedAnswerCallback(queryId, text, isAlert);
    }
    public void sendAnswer(String queryId, String text) {
        sendAnswer(queryId, text, false);
    }
    public void sendAnswer(String queryId) {
        sendAnswer(queryId, null, false);
    }


    private void proceedMessageSend(String text, ReplyKeyboard keyboard, String userId, Boolean useMarkdown) {
        SendMessage message = new SendMessage()
                .enableMarkdown(useMarkdown)
                .setChatId(userId)
                .setText(text)
                .setReplyMarkup(keyboard);
        execute(message);
    }
    private void proceedMessageSend(TextResponseMessage messageTemplate) {
        SendMessage message = new SendMessage()
                .enableMarkdown(messageTemplate.isApplyMarkup())
                .setChatId(messageTemplate.getTargetId())
                .setText(messageTemplate.getText())
                .setReplyMarkup(messageTemplate.getKeyboard());
        execute(message);
    }
    private void proceedImageSend(File file, String fileName, InputStream stream, String fileId, String userId, String caption) {
        SendPhoto newMessage = new SendPhoto()
                .setCaption(caption)
                .setChatId(userId);
        if (file != null) {
            newMessage.setPhoto(file);
        } else if (stream != null) {
            newMessage.setPhoto(fileName, stream);
        } else if (fileId != null) {
            newMessage.setPhoto(fileId);
        }
        execute(newMessage);
    }
    private void proceedImageSend(ImageResponseMessage messageTemplate) {
        SendPhoto newMessage = new SendPhoto()
                .setCaption(messageTemplate.getCaption())
                .setChatId(messageTemplate.getTargetId());
        if (messageTemplate.getFile() != null) {
            newMessage.setPhoto(messageTemplate.getFile());
        } else if (messageTemplate.getInputStream() != null) {
            newMessage.setPhoto(messageTemplate.getFileName(), messageTemplate.getInputStream());
        } else if (messageTemplate.getFileId() != null) {
            newMessage.setPhoto(messageTemplate.getFileId());
        }
        execute(newMessage);
    }
    private void proceedDocumentSend(File file, String userId) {
        SendDocument sendMessage = new SendDocument()
                .setDocument(file)
                .setChatId(userId);
        execute(sendMessage);
    }
    private void proceedDocumentSend(DocumentResponseMessage messageTemplate) {
        SendDocument sendMessage = new SendDocument()
                .setDocument(messageTemplate.getFile())
                .setChatId(messageTemplate.getTargetId());
        execute(sendMessage);
    }
    private void proceedStickerSend(StickerResponseMessage messageTemplate) {
        SendSticker newMessage = new SendSticker()
                .setChatId(messageTemplate.getTargetId());
        if (messageTemplate.getFile() != null) {
            newMessage.setSticker(messageTemplate.getFile());
        } else if (messageTemplate.getInputStream() != null) {
            newMessage.setSticker(messageTemplate.getFileName(), messageTemplate.getInputStream());
        } else if (messageTemplate.getFileId() != null) {
            newMessage.setSticker(messageTemplate.getFileId());
        }
        execute(newMessage);
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
    private void execute(SendSticker method) {
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
