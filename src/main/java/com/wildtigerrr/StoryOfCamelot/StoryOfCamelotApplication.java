package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
public class StoryOfCamelotApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StoryOfCamelotApplication.class, args);
        new ResponseManager().postMessageToAdminChannel("Bot Started");
        addShutdownHook();
    }

    // Actions before restart
    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            TimeDependentActions.backupValues();
            new ResponseManager().postMessageToAdminChannel("Bot Shutting Down");
        }));
    }

}