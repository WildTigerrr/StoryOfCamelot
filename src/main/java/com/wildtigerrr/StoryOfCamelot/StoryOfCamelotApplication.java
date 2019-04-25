package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import com.wildtigerrr.StoryOfCamelot.web.service.TimeDependentActions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
@EnableAutoConfiguration
public class StoryOfCamelotApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StoryOfCamelotApplication.class, args);
        TimeDependentActions.restoreValues();
        new ResponseHandler().sendMessageToAdmin("Bot Started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            TimeDependentActions.backupValues();
            new ResponseHandler().sendMessageToAdmin("Bot Shutting Down");
            System.out.println("Shutdown Hook Added");
        }));
    }

}