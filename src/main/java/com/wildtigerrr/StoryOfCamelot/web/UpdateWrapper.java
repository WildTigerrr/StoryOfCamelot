package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class UpdateWrapper {

    private String message;
    private String userId;
    private Long chatId;
    private int messageId;
    private String queryId;
    private String firstName;
    private String lastName;
    private String username;
    private String language;
    private Player player;
    private Boolean isQuery;

    // TODO Add escaped entire Update for debug and logs

    UpdateWrapper(Update update, Boolean isQuery) {
        User user = isQuery ? update.getCallbackQuery().getMessage().getFrom() : update.getMessage().getFrom();
        this.message = StringUtils.escape(isQuery ? update.getCallbackQuery().getData() : update.getMessage().getText().trim());
        if (this.message.contains("@story_of_camelot_bot")) this.message = this.message.replace("@story_of_camelot_bot", "").trim();
        this.firstName = user.getFirstName();
        this.userId = isQuery ? update.getCallbackQuery().getMessage().getChatId().toString() : user.getId().toString();
        this.chatId = isQuery ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
        this.messageId = isQuery ? update.getCallbackQuery().getMessage().getMessageId() : update.getMessage().getMessageId();
        this.queryId = isQuery ? update.getCallbackQuery().getId() : null;
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

    public Long getChatId() {
        return chatId;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getQueryId() {
        return queryId;
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

    public String getLanguage() {
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
                ", languageCode='" + language + '\'' +
                ", isQuery='" + isQuery + '\'' +
                '}';
    }
}
