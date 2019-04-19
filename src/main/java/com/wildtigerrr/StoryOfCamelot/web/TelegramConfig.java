package com.wildtigerrr.StoryOfCamelot.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.Update;

@Configuration
public class TelegramConfig {
    @Bean
    public Update update() {
        return new Update();
    }
}
