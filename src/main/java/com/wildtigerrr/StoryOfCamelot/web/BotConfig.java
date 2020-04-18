package com.wildtigerrr.StoryOfCamelot.web;

public class BotConfig {

    public static String WEBHOOK_TOKEN = System.getenv("TG_TOKEN");

    public static String WEBHOOK_USER = System.getenv("TG_BOT_NAME");
    public static String WEBHOOK_ADMIN = System.getenv("TG_ADMIN_NAME");

    public static String WEBHOOK_ADMIN_ID = System.getenv("TG_ADMIN_ID");
    public static String ADMIN_CHANNEL_ID = "-1001419651307";

    public final static int DAMAGE_RANDOM = 60; // In %
    public final static float ARMOR_EFFECTIVENESS = 33.0f;
    public final static float CRIT_MULTIPLIER = 1.5f;
    public final static int DEFAULT_SKILL_POINTS = 20;

}
