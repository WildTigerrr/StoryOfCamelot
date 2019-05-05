package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import com.wildtigerrr.StoryOfCamelot.web.service.LocalRedisConfig;
import com.wildtigerrr.StoryOfCamelot.web.service.TimeDependentActions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Properties;

@Controller
@SpringBootApplication
//@Import(LocalRedisConfig.class)
public class StoryOfCamelotApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StoryOfCamelotApplication.class, args);
        addRedisProperties();
        new ResponseHandler().sendMessageToAdmin("Bot Started");
        addShutdownHook();
    }

    // Actions before restart
    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            TimeDependentActions.backupValues();
            new ResponseHandler().sendMessageToAdmin("Bot Shutting Down");
            System.out.println("Shutdown Hook Added");
        }));
    }

    private static void addRedisProperties() {
        try {
            URI redisURI = new URI(System.getenv("REDIS_URL"));
            Properties props = new Properties();
            props.setProperty("spring.data.redis.repositories.enabled", "true");
            props.setProperty("spring.redis.host", redisURI.getHost());
            props.setProperty("spring.redis.password", redisURI.getUserInfo().split(":",2)[1]);
            props.setProperty("spring.redis.ssl", "true");
//            props.setProperty("spring.redis.pool.max-active", "10");
//            props.setProperty("spring.redis.pool.max-idle", "5");
            props.setProperty("spring.redis.pool.max-wait", "30000");
            props.setProperty("spring.redis.port", String.valueOf(redisURI.getPort()));
            File f = new File("application.properties");
            OutputStream out = new FileOutputStream( f );
            // write into it
            DefaultPropertiesPersister p = new DefaultPropertiesPersister();
            p.store(props, out, "Redis Settings");
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

}