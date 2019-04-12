package com.wildtigerrr.StoryOfCamelot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@Controller
@SpringBootApplication
public class StoryOfCamelotApplication {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SpringApplication.run(StoryOfCamelotApplication.class, args);
    }

}
