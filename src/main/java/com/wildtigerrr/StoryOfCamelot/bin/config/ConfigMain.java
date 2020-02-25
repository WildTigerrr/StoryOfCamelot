package com.wildtigerrr.StoryOfCamelot.bin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("!local")
@PropertySource("classpath:application.properties")
public class ConfigMain {
}
