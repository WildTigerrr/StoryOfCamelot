package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Log4j2
public class UpdateReceiver implements Runnable {

    public final Queue<UpdateWrapper> messages = new ConcurrentLinkedQueue<>();
    private boolean active;

    @Override
    public void run() {
        int messagesCount = 0;
        active = true;
        while(active) {
            if (messages.size() != messagesCount) {
                messagesCount = messages.size();
                log.info("Messages queue: " + messagesCount);
            }
        }
    }

    public void process(UpdateWrapper update) {
        messages.add(update);
    }

    @PreDestroy
    public void destroy() {
        log.info("Incoming messages should be stored before restart");
        active = false;
    }

}