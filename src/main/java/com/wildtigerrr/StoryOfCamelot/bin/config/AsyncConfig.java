package com.wildtigerrr.StoryOfCamelot.bin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("threadExecutor")
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        System.out.println(Runtime.getRuntime().availableProcessors());
        executor.setCorePoolSize(Math.min(4, Runtime.getRuntime().availableProcessors()));
        executor.setMaxPoolSize(Math.min(10, Runtime.getRuntime().availableProcessors()));
        executor.setThreadNamePrefix("default_task_executor_thread");
        executor.initialize();
        return executor;
    }

}
