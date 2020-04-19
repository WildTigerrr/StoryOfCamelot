package com.wildtigerrr.StoryOfCamelot.bin.service;

import com.wildtigerrr.StoryOfCamelot.web.UpdateReceiver;
import com.wildtigerrr.StoryOfCamelot.web.service.impl.TelegramResponseManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Log4j2
@DependsOn("applicationContextProvider")
public class AsynchronousService {

    private final TaskExecutor executor;

    public AsynchronousService(TaskExecutor threadExecutor) {
        this.executor = threadExecutor;
    }

    @PostConstruct
    public void startAsyncServices() {
        log.info("Starting async services");
        launchReceiverService();
        launchSenderService();
    }

    private void launchReceiverService() {
        log.info("Starting UpdateReceiver");
        UpdateReceiver receiver = ApplicationContextProvider.bean("updateReceiver");
        executor.execute(receiver);
    }

    private void launchSenderService() {
        log.info("Starting TelegramResponseManager");
        TelegramResponseManager sender = ApplicationContextProvider.bean("telegramResponseManager");
        executor.execute(sender);
    }

}
