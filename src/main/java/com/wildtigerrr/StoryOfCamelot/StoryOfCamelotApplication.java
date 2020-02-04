package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.impl.TelegramResponseManager;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.TimeDependentActions;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Log4j2
@Controller
@SpringBootApplication
public class StoryOfCamelotApplication {

    // TODO Add S3 Appender, for example https://github.com/bluedenim/log4j-s3-search

    private final ResponseManager messages;

    public StoryOfCamelotApplication(ResponseManager responseManager) {
        this.messages = responseManager;
    }

    public static void main(String[] args) {
        log.info("Starting Application");
        try {
            SpringApplication.run(StoryOfCamelotApplication.class, args);
        } catch (Exception e) {
            onRunFailure(e);
        }
    }

    @PostConstruct
    private void onAfterRun() {
        log.debug("Sending Application Started Notification");
        messages.postMessageToAdminChannel("Bot Started");
        log.info("Application Started Successfully");
    }

    private static void onRunFailure(Exception e) {
        log.debug("Sending Startup Failure Notification");
        // TODO Remove direct call
        new TelegramResponseManager().postMessageToAdminChannel("Exception during startup: " + e.getMessage());
        log.fatal(e);
    }

    @PreDestroy
    private void onBeforeRestart() {
        log.warn("Restarting Heroku Server");
        TimeDependentActions.backupValues();
        messages.postMessageToAdminChannel("Bot Shutting Down");
    }

}