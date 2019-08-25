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
        logger.debug("Logger enabled");
        try {
            logger.debug("Logger enabled 2");
            SpringApplication.run(StoryOfCamelotApplication.class, args);
            logger.debug("Logger enabled 3");
            onAfterRun();
        } catch (Exception e) {
            onRunFailure(e);
        }
    }

    private static void onAfterRun() {
        ResponseManager.postMessageToAdminChannel("Bot Started");
        logger.debug("Logger enabled 4");
        addShutdownHook();
    }

    private static void onRunFailure(Exception e) {
        ResponseManager.postMessageToAdminChannel("Exception during startup: " + e.getMessage());
        e.printStackTrace();
    }

    private static void onBeforeRestart() {
        TimeDependentActions.backupValues();
        ResponseManager.postMessageToAdminChannel("Bot Shutting Down");
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(StoryOfCamelotApplication::onBeforeRestart));
    }

}