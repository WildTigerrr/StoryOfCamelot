package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class UpdateWrapper {

    @Autowired
    private PlayerServiceImpl playerDao;

    private String message;
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String language;
    private Player player;

    public UpdateWrapper(Update update) {
        User user = update.getMessage().getFrom();
        this.message = update.getMessage().getText();
        this.userId = user.getId().toString();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUserName();
        this.language = user.getLanguageCode();

        Player player = playerDao.findByExternalId(this.userId);
        if (player == null) {
            player = new Player(this.userId, this.userId);
            player = playerDao.create(player);
        }
        this.player = player;
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

    public Player getPlayer() {
        return player;
    }
}
