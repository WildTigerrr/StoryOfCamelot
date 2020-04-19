package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
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

    public final Queue<IncomingMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private boolean active;

    public UpdateReceiver(ResponseHandler responseHandler, ResponseManager messages) {
        this.responseHandler = responseHandler;
        this.messages = messages;
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

    public void process(Update update) {
        messageQueue.add(IncomingMessage.from(update));
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