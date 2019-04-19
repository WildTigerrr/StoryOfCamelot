package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class UpdateWrapper {

    private String message;
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String language;
    private Player player;

    public UpdateWrapper(Update update) {
        User user = update.getMessage().getFrom();
        this.message = update.getMessage().getText().trim();
        this.userId = user.getId().toString();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUserName();
        this.language = user.getLanguageCode();
    }

    public String getText() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getLanguage() {
        return language;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
