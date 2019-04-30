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
    private Boolean isQuery;

    UpdateWrapper(Update update, Boolean isQuery) {
        User user = update.getMessage().getFrom();
        this.message = isQuery ? update.getCallbackQuery().getData() : update.getMessage().getText().trim();
        this.userId = user.getId().toString();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUserName();
        this.language = user.getLanguageCode();
        this.isQuery = isQuery;
    }

    String getText() {
        return message;
    }

    String getUserId() {
        return userId;
    }

    String getFirstName() {
        return firstName;
    }

    String getLastName() {
        return lastName;
    }

    String getUsername() {
        return username;
    }

    String getLanguage() {
        return language;
    }

    public Boolean isQuery() {
        return isQuery;
    }

    void setPlayer(Player player) {
        this.player = player;
    }

    Player getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "UpdateWrapper{" +
                "message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
