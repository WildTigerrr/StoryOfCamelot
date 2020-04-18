package com.wildtigerrr.StoryOfCamelot.bin.service;

import com.wildtigerrr.StoryOfCamelot.web.UpdateReceiver;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Log4j2
public class AsynchronousService {

    private final TaskExecutor executor;

    public AsynchronousService(TaskExecutor threadExecutor) {
        this.executor = threadExecutor;
    }

    @PostConstruct
    public void startAsyncServices() {
        log.info("Starting async services");
        UpdateReceiver receiver = ApplicationContextProvider.bean("updateReceiver");
        executor.execute(receiver);
    }

}
