package com.wildtigerrr.StoryOfCamelot.web.bot.update;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.bot.utils.UpdateWrapperUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class UpdateWrapper {

    private String message;
    private Long chatId;
    private int messageId;
    private String queryId;
    private Author author;
    private String language;
    private Player player;
    private Boolean isQuery;

    // TODO Add escaped entire Update for debug and logs

    public UpdateWrapper(Update update, Boolean isQuery) {
        User user = isQuery ? update.getCallbackQuery().getMessage().getFrom() : update.getMessage().getFrom();
        this.message = isQuery ? update.getCallbackQuery().getData() : StringUtils.escape(update.getMessage().getText().trim());
        if (this.message.contains("@StoryOfCamelotBot")) this.message = this.message.replace("@StoryOfCamelotBot", "").trim();
        this.author = new Author(user);
        if (isQuery) author.setId(update.getCallbackQuery().getMessage().getChatId().toString());
        this.chatId = isQuery ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
        this.messageId = isQuery ? update.getCallbackQuery().getMessage().getMessageId() : update.getMessage().getMessageId();
        this.queryId = isQuery ? update.getCallbackQuery().getId() : null;
        this.language = user.getLanguageCode();
        this.isQuery = isQuery;
    }

    public String getText() {
        return message;
    }

    public String getUserId() {
        return author.getId();
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
        return author.getFirstName();
    }

    String getLastName() {
        return author.getLastName();
    }

    String getUsername() {
        return author.getUsername();
    }

    public String getLanguage() {
        return language;
    }

    public Boolean isQuery() {
        return isQuery;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Command getCommand() {
        return UpdateWrapperUtils.fetchCommandFromMessage(message, player.getLanguage());
    }

    @Override
    public String toString() {
        return "UpdateWrapper{" +
                "message='" + message + '\'' +
                ", userId='" + author.getId() + '\'' +
                ", firstName='" + author.getFirstName() + '\'' +
                ", lastName='" + author.getLastName() + '\'' +
                ", username='" + author.getUsername() + '\'' +
                ", languageCode='" + language + '\'' +
                ", isQuery='" + isQuery + '\'' +
                '}';
    }
}
