package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
public class StoryOfCamelotApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(StoryOfCamelotApplication.class, args);
            onAfterRun();
        } catch (Exception e) {
            onRunFailure(e);
        }
    }

    private static void onAfterRun() {
        ResponseManager.postMessageToAdminChannel("Bot Started");
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