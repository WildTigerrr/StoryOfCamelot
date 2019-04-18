package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.daointerface.PlayerDaoInterface;
import com.wildtigerrr.StoryOfCamelot.web.BotResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
//@ComponentScan({
//        "com.wildtigerrr.StoryOfCamelot.web"
//        , "com.wildtigerrr.StoryOfCamelot"
//        , "com.wildtigerrr.StoryOfCamelot.database.schema"
//        , "com.wildtigerrr.StoryOfCamelot.service"
//})
//@EnableJpaRepositories("com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.daointerface")
@EnableAutoConfiguration
public class StoryOfCamelotApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StoryOfCamelotApplication.class, args);
        new BotResponseHandler().sendMessageToAdmin("Bot Started");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                new BotResponseHandler().sendMessageToAdmin("Bot Shutting Down");
                System.out.println("Shutdown Hook Added");
            }
        });
    }

}
