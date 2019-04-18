package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.daointerface.PlayerDaoInterface;
import com.wildtigerrr.StoryOfCamelot.web.BotResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@ComponentScan("com.wildtigerrr.StoryOfCamelot.database.schema")
public class StoryOfCamelotApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StoryOfCamelotApplication.class, args);
        BotResponseHandler.sendMessageToAdmin("Bot Started");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                BotResponseHandler.sendMessageToAdmin("Bot Shutting Down");
                System.out.println("Shutdown Hook Added");
            }
        });
    }

}
