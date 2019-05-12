package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class UpdateWrapper {

    private String message;
    private String userId;
    private int messageId;
    private String firstName;
    private String lastName;
    private String username;
    private String language;
    private Player player;
    private Boolean isQuery;

    UpdateWrapper(Update update, Boolean isQuery) {
        User user = isQuery ? update.getCallbackQuery().getMessage().getFrom() : update.getMessage().getFrom();
        this.message = isQuery ? update.getCallbackQuery().getData() : update.getMessage().getText().trim();
        this.userId = isQuery ? update.getCallbackQuery().getMessage().getChatId().toString() : user.getId().toString();
        this.messageId = isQuery ? update.getCallbackQuery().getMessage().getMessageId() : update.getMessage().getMessageId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUserName();
        this.language = user.getLanguageCode();
        this.isQuery = isQuery;
    }

    public String getText() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public int getMessageId() {
        return messageId;
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

    public Player getPlayer() {
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
