package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
public class StoryOfCamelotApplication {

    private static final Logger logger = LogManager.getLogger(StoryOfCamelotApplication.class);


    public static void main(String[] args) {
        logger.info("Starting Application");
        try {
            SpringApplication.run(StoryOfCamelotApplication.class, args);
            onAfterRun();
        } catch (Exception e) {
            onRunFailure(e);
        }
    }

    private static void onAfterRun() {
        logger.debug("Sending Application Started Notification");
        ResponseManager.postMessageToAdminChannel("Bot Started");
        addShutdownHook();
        logger.info("Application Started Successfully");
    }

    private static void onRunFailure(Exception e) {
        logger.debug("Sending Startup Failure Notification");
        ResponseManager.postMessageToAdminChannel("Exception during startup: " + e.getMessage());
        logger.fatal(e);
    }

    private static void onBeforeRestart() {
        logger.warn("Restarting Heroku Server");
        TimeDependentActions.backupValues();
        ResponseManager.postMessageToAdminChannel("Bot Shutting Down");
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(StoryOfCamelotApplication::onBeforeRestart));
    }

}