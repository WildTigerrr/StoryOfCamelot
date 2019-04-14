package com.wildtigerrr.StoryOfCamelot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;

@Controller
@SpringBootApplication
public class StoryOfCamelotApplication {
    //@Configuration
    //@ComponentScan
    //@EnableAutoConfiguration

    // Maven > Plugins > heroku > heroku:deploy to create dependencies
    // heroku buildpacks:clear
    // git push heroku master
    //mvnw clean install

//    static String helloText = "Hello World!";

    @RequestMapping("/")
    @ResponseBody
    String home() {
//        System.out.println("Hey, I'll give you Hello World!");
//        return helloText;
        return "Hello world!";
    }

    public static void main(String[] args) {
        SpringApplication.run(StoryOfCamelotApplication.class, args);
    }

}
