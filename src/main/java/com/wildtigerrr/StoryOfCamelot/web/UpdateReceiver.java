package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PreDestroy;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Log4j2
public class UpdateReceiver implements Runnable {

    private final GameMain gameMain;

    public final Queue<UpdateWrapper> messages = new ConcurrentLinkedQueue<>();
    private boolean active;

    public UpdateReceiver(GameMain gameMain) {
        this.gameMain = gameMain;
    }

    @Override
    public void run() {
        active = true;
        while(active) {
            for (UpdateWrapper object = messages.poll(); object != null; object = messages.poll()) {
                gameMain.handleTextMessage(object);
            }
        }
    }

    public void process(UpdateWrapper update) {
        messages.add(update);
    }

    public void process(Update update) {
        messages.add(new UpdateWrapper(update));
    }

    @PreDestroy
    public void destroy() {
        log.info("Incoming messages should be stored before restart");
        active = false;
    }

}