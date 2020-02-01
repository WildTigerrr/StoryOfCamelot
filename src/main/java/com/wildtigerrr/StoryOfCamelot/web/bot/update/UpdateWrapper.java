package com.wildtigerrr.StoryOfCamelot.web.bot.update;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.bot.utils.UpdateWrapperUtils;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Getter
public class UpdateWrapper {

    private String message;
    private Long chatId;
    private int messageId;
    private String queryId;
    private Author author;
    private String userLanguageCode;
    private Player player;
    private Boolean isQuery;
    private Command command;

    private UpdateType updateType;

    // TODO Add escaped entire Update for debug and logs

    public UpdateWrapper(Update update) {
        this.isQuery = update.hasCallbackQuery();
        User user = isQuery ? update.getCallbackQuery().getMessage().getFrom() : update.getMessage().getFrom();
        this.updateType = UpdateWrapperUtils.defineUpdateType(update);
        if (isCommand()) {
            this.message = isQuery ? update.getCallbackQuery().getData() : StringUtils.escape(update.getMessage().getText().trim());
            if (this.message.contains("@StoryOfCamelotBot")) this.message = this.message.replace("@StoryOfCamelotBot", "").trim();
        }
        this.author = new Author(user);
        if (isQuery) author.setId(update.getCallbackQuery().getMessage().getChatId().toString());
        this.chatId = isQuery ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
        this.messageId = isQuery ? update.getCallbackQuery().getMessage().getMessageId() : update.getMessage().getMessageId();
        this.queryId = isQuery ? update.getCallbackQuery().getId() : null;
        this.userLanguageCode = user.getLanguageCode();
    }

    public boolean isCommand() {
        return updateType == UpdateType.MESSAGE || updateType == UpdateType.CALLBACK;
    }

    public boolean isUnsupportedMedia() {
        return !isCommand() && updateType != UpdateType.OTHER;
    }

    public String getText() {
        return message;
    }

    public String getUserId() {
        return author.getId();
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

    public Boolean isQuery() {
        return isQuery;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Command getCommand() {
        if (command == null) command = UpdateWrapperUtils.fetchCommandFromMessage(message, player.getLanguage());
        return command;
    }

    @Override
    public String toString() {
        return "UpdateWrapper{" +
                " type='" + updateType + '\'' +
                ", message='" + message + '\'' +
                ", userId='" + author.getId() + '\'' +
                ", firstName='" + author.getFirstName() + '\'' +
                ", lastName='" + author.getLastName() + '\'' +
                ", username='" + author.getUsername() + '\'' +
                ", languageCode='" + userLanguageCode + '\'' +
                ", isQuery='" + isQuery + '\'' +
                '}';
    }
}
