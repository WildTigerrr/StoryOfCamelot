package com.wildtigerrr.StoryOfCamelot.web;

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

    public final Queue<IncomingMessage> messages = new ConcurrentLinkedQueue<>();
    private boolean active;

    public UpdateReceiver(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void run() {
        active = true;
        while(active) {
            for (IncomingMessage message = messages.poll(); message != null; message = messages.poll()) {
                responseHandler.proceed(message);
            }
        }
    }

    public void process(Update update) {
        messages.add(IncomingMessage.from(update));
    }

    @PreDestroy
    public void destroy() {
        log.info("Incoming messages should be stored before restart");
        active = false;
    }

}