package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@SpringBootApplication
public class StoryOfCamelotApplication {

    // TODO Add S3 Appender, for example https://github.com/bluedenim/log4j-s3-search


    public static void main(String[] args) {
        log.info("Starting Application");
        try {
            SpringApplication.run(StoryOfCamelotApplication.class, args);
            onAfterRun();
        } catch (Exception e) {
            onRunFailure(e);
        }
    }

    private static void onAfterRun() {
        log.debug("Sending Application Started Notification");
        ResponseManager.postMessageToAdminChannelOnStart("Bot Started");
        addShutdownHook();
        log.info("Application Started Successfully");
    }

    private static void onRunFailure(Exception e) {
        log.debug("Sending Startup Failure Notification");
        ResponseManager.postMessageToAdminChannelOnStart("Exception during startup: " + e.getMessage());
        log.fatal(e);
    }

    private static void onBeforeRestart() {
        log.warn("Restarting Heroku Server");
        TimeDependentActions.backupValues();
        ResponseManager.postMessageToAdminChannelOnStart("Bot Shutting Down");
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(StoryOfCamelotApplication::onBeforeRestart));
    }

}