package com.wildtigerrr.StoryOfCamelot.database;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseActionsHandler implements CommandLineRunner {

    @Override
    public void run(String...args) throws Exception {
        System.out.println("DB fill attempt");
        new DatabaseInteraction().insertInitialData();
    }

}
