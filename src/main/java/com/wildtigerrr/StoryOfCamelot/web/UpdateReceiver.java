package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PreDestroy;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Log4j2
public class UpdateReceiver implements Runnable {

    private final ResponseHandler responseHandler;
    private final ResponseManager messages;
    private final PlayerService playerService;

    public final Queue<IncomingMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private boolean active;

    public UpdateReceiver(ResponseHandler responseHandler, ResponseManager messages, PlayerService playerService) {
        this.responseHandler = responseHandler;
        this.messages = messages;
        this.playerService = playerService;
    }

    @Override
    public void run() {
        active = true;
        while (active) {
            for (IncomingMessage message = messageQueue.poll(); message != null; message = messageQueue.poll()) {
                try {
                    responseHandler.proceed(message);
                } catch (Exception e) {
                    onError(message, e);
                }
            }
        }
    }

    public void process(IncomingMessage message) {
        logSender(message);
        messageQueue.add(message);
    }

    public void process(Update update) {
        IncomingMessage message = IncomingMessage.from(update);
        message.setPlayer(playerService.getPlayer(message.getUserId()));
        process(message);
    }

    private void logSender(IncomingMessage message) {
        log.info(message.senderLog() + ": " + message.text() + " - Queued");
        if (!message.getUserId().equals(BotConfig.WEBHOOK_ADMIN_ID)) {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(message.toString()).type(ResponseType.POST_TO_ADMIN_CHANNEL).build()
            );
        }
    }

    private void onError(IncomingMessage message, Exception e) {
        sendErrorReport(
                "Exception during runtime: `" + e.getMessage() + "`; " +
                        "\n\nDuring working on message: `" + message + "`", e);
    }

    private void sendErrorReport(String message, Exception e) {
        log.error(message, e);
        messages.postMessageToAdminChannel(message, true);
    }

    @PreDestroy
    public void destroy() {
        log.info("Incoming messages should be stored before restart");
        active = false;
    }

}