package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication(scanBasePackages = {"org.telegram.telegrambots.meta.api.objects"})
@EnableAutoConfiguration
public class StoryOfCamelotApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StoryOfCamelotApplication.class, args);
        new ResponseHandler().sendMessageToAdmin("Bot Started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            new ResponseHandler().sendMessageToAdmin("Bot Shutting Down");
            System.out.println("Shutdown Hook Added");
        }));
    }

}
