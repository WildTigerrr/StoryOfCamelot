package com.wildtigerrr.StoryOfCamelot.web.service.impl;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.service.ApplicationContextProvider;
import com.wildtigerrr.StoryOfCamelot.bin.service.Time;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.TelegramWebHookHandler;
import com.wildtigerrr.StoryOfCamelot.web.UpdateReceiver;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PreDestroy;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
@Service("responseManager")
@Profile("!test")
public class TelegramResponseManager implements ResponseManager, Runnable {

    private TranslationManager translation;
    private TelegramWebHookHandler webHook;
    private UpdateReceiver receiver;
    private final Map<String, MessageQueue> responsesByChat = new ConcurrentHashMap<>();
    private boolean alreadyRedirected = false;
    private boolean active;

    public void setExecutor(TelegramWebHookHandler telegramWebHookHandler) {
        this.webHook = telegramWebHookHandler;
    }

    @Override
    public void run() {
        active = true;
        while(active) {
            for (MessageQueue queue : responsesByChat.values()) {
                if (queue.hasMessage()) {
                    try {
                        proceed(queue.get());
                    } catch (Exception e) {
                        queue.reportError(e);
                    }
                }
            }
        }
    }

    public void sendMessage(ResponseMessage message) {
        if (responsesByChat.containsKey(message.getTargetId())) {
            responsesByChat.get(message.getTargetId()).add(message);
        } else {
            responsesByChat.put(message.getTargetId(), new MessageQueue(message));
        }
    }

    private UpdateReceiver receiver() {
        if (receiver == null) receiver = ApplicationContextProvider.bean("updateReceiver");
        return receiver;
    }

    private TranslationManager translation() {
        if (translation == null) translation = ApplicationContextProvider.bean("translationManager");
        return translation;
    }

    private void proceed(ResponseMessage message) {
        switch (message.getType()) {
            case TEXT:
            case POST_TO_ADMIN_CHANNEL:
                proceedMessageSend((TextResponseMessage) message); break;
            case PHOTO: proceedImageSend((ImageResponseMessage) message); break;
            case DOCUMENT: proceedDocumentSend((DocumentResponseMessage) message); break;
            case STICKER: proceedStickerSend((StickerResponseMessage) message); break;
            case EDIT: proceedMessageEdit((EditResponseMessage) message); break;
            case DICE: proceedDiceSend((DiceResponseMessage) message); break;
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
        sendMessage(TextResponseMessage.builder().lang(Language.RUS)
                .targetId(BotConfig.ADMIN_CHANNEL_ID)
                .type(ResponseType.POST_TO_ADMIN_CHANNEL)
                .text(text)
                .disableNotification(true)
                .applyMarkup(applyMarkup).build()
        );
    }
    public void postMessageToAdminChannel(String text) {
        postMessageToAdminChannel(text, false);
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


    private void proceedMessageSend(TextResponseMessage messageTemplate) {
        SendMessage message = SendMessage.builder()
                .chatId(messageTemplate.getType() == ResponseType.POST_TO_ADMIN_CHANNEL
                        ? BotConfig.ADMIN_CHANNEL_ID : messageTemplate.getTargetId())
                .text(messageTemplate.getText())
                .replyMarkup(messageTemplate.getKeyboard()).build();
        message.enableMarkdown(messageTemplate.isApplyMarkup());
        if (messageTemplate.isDisableNotification()) message.disableNotification();
        execute(message);
    }
    private void proceedImageSend(ImageResponseMessage messageTemplate) {
        SendPhoto newMessage = SendPhoto.builder()
                .caption(messageTemplate.getCaption())
                .chatId(messageTemplate.getTargetId())
                .replyMarkup(messageTemplate.getKeyboard())
                .photo(messageTemplate.getInputFile())
                .build();
        if (messageTemplate.isDisableNotification()) newMessage.disableNotification();
        execute(newMessage);
    }
    private void proceedDocumentSend(DocumentResponseMessage messageTemplate) {
        SendDocument sendMessage = SendDocument.builder()
                .document(messageTemplate.getInputFile())
                .chatId(messageTemplate.getTargetId())
                .build();
        if (messageTemplate.isDisableNotification()) sendMessage.disableNotification();
        execute(sendMessage);
    }
    private void proceedStickerSend(StickerResponseMessage messageTemplate) {
        SendSticker newMessage = SendSticker.builder()
                .chatId(messageTemplate.getTargetId())
                .sticker(messageTemplate.getInputFile())
                .build();
        if (messageTemplate.isDisableNotification()) newMessage.disableNotification();
        execute(newMessage);
    }
    private void proceedMessageEdit(EditResponseMessage messageTemplate) {
        EditMessageText messageEdit = EditMessageText.builder()
                .messageId(messageTemplate.getMessageId())
                .chatId(messageTemplate.getTargetId())
                .text(messageTemplate.getText())
                .replyMarkup(messageTemplate.getKeyboard()).build();
        messageEdit.enableMarkdown(messageTemplate.isApplyMarkup());
        execute(messageEdit);
    }
    private void proceedAnswerCallback(String queryId, String text,Boolean isAlert) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId)
                .showAlert(isAlert)
                .text(text)
                .build();
        execute(answerCallbackQuery);
    }

    private void proceedDiceSend(DiceResponseMessage messageTemplate) {
        SendDice newMessage = SendDice.builder()
                .emoji(messageTemplate.getEmoji())
                .chatId(messageTemplate.getTargetId())
                .build();
        Message response = execute(newMessage);
        DiceIncomingMessage message = messageTemplate.getIncomingMessage();
        message.setResponse(response.getDice().getValue());
        receiver().process(message);
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
    private Message execute(SendDice method) {
        try {
            return webHook.execute(method);
        } catch (TelegramApiException e) {
            handleError(e);
            return null;
        }
    }

    private void executeBeforeAutowiring(BotApiMethod method) {
        log.warn("Spring Startup Error (Autowired Services not initialized)");
        try {
            new TelegramWebHookHandler().execute(method);
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

    @PreDestroy
    public void destroy() {
        log.info("Outgoing messages should be stored before restart");
        active = false;
    }

    class MessageQueue {
        private final String targetId;

        private final List<Long> timestamps = new CopyOnWriteArrayList<>();
        private final Deque<ResponseMessage> responses = new ConcurrentLinkedDeque<>();

        private Language language;
        private long nextMessageOn;
        private ResponseMessage lastMessage;
        private boolean postponeNext;


        MessageQueue(String targetId, Language language) {
            this.targetId = targetId;
            this.language = language;
        }
        MessageQueue(ResponseMessage message) {
            this(message.getTargetId(), message.getLanguage());
            add(message);
        }

        void add(ResponseMessage message) {
            long currentTime = System.currentTimeMillis();
            clearOldTimestamps(currentTime);
            timestamps.add(currentTime);
            responses.add(message);
            language = message.getLanguage();
            checkSpam();
        }

        ResponseMessage get() {
            long time = System.currentTimeMillis();
            if (time > nextMessageOn) {
                if (postponeNext) {
                    nextMessageOn = time + Time.seconds(20);
                    postponeNext = false;
                } else {
                    nextMessageOn = time + Time.seconds(BotConfig.MESSAGE_DELAY);
                }
                lastMessage = responses.pollFirst();
                return lastMessage;
            }
            return null;
        }

        boolean hasMessage() {
            return !responses.isEmpty() && (System.currentTimeMillis() > nextMessageOn);
        }

        void reportError(Exception e) {
            log.error("Message can't be sent: " + lastMessage, e);
            responses.offerFirst(lastMessage);
            nextMessageOn = System.currentTimeMillis() + Time.seconds(5);
        }

        private void clearOldTimestamps(Long now) {
            timestamps.removeIf(time -> now - time > Time.seconds(20));
        }

        private void checkSpam() {
            if (timestamps.size() > 20 && !postponeNext) {
                responses.offerFirst(TextResponseMessage.builder().lang(language)
                        .text(translation().getMessage("commands.too-fast"))
                        .targetId(targetId)
                        .build()
                );
                postponeNext = true;
            }
        }

    }

}
