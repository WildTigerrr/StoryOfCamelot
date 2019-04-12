package com.wildtigerrr.StoryOfCamelot;

import org.springframework.boot.CommandLineRunner;

public class SOCBotInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        main(args);
    }

    public static void main(String[] args) {
        // My Logic
        System.out.println("TEST DEBUG!!!");
        System.out.println(args);
    }

}
