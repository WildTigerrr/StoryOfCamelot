package com.wildtigerrr.StoryOfCamelot.web;

import org.springframework.beans.factory.annotation.Value;

public class BotConfig {
    @Value("TG_TOKEN")
    private static String WEBHOOK_TOKEN;

    public static String getWebhookToken() {
        String token = System.getenv("TG_TOKEN");
        return token == null ? WEBHOOK_TOKEN : token;
    }

    public static String WEBHOOK_USER = "story_of_camelot_bot";
    public static String WEBHOOK_ADMIN = "WildTigerrr";
    public static String BOT_NAME = "WildTigerrr";

    public static String WEBHOOK_ADMIN_ID = "413316947";
    public static String ADMIN_CHANNEL_ID = "-1001419651307";

    public final static int DAMAGE_RANDOM = 60; // In %
    public final static float ARMOR_EFFECTIVENESS = 33.0f;
    public final static float CRIT_MULTIPLIER = 1.5f;
    public final static int DEFAULT_SKILL_POINTS = 20;

}
