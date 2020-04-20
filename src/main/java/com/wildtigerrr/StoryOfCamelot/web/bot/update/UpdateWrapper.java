package com.wildtigerrr.StoryOfCamelot.web.bot.update;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
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

    private MessageType messageType;

    // TODO Add escaped entire Update for debug and logs

    public UpdateWrapper(Update update) {
        this.isQuery = update.hasCallbackQuery();
        User user = isQuery ? update.getCallbackQuery().getMessage().getFrom() : update.getMessage().getFrom();
        this.messageType = UpdateWrapperUtils.defineUpdateType(update);
        if (isCommand()) {
            this.message = isQuery ? update.getCallbackQuery().getData() : StringUtils.escape(update.getMessage().getText().trim());
            if (this.message.contains("@StoryOfCamelotBot")) this.message = this.message.replace("@StoryOfCamelotBot", "").trim();
        }
        this.author = new Author(user);
        if (isQuery) author.setExternalId(update.getCallbackQuery().getMessage().getChatId().toString());
        this.chatId = isQuery ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
        this.messageId = isQuery ? update.getCallbackQuery().getMessage().getMessageId() : update.getMessage().getMessageId();
        this.queryId = isQuery ? update.getCallbackQuery().getId() : null;
        this.userLanguageCode = user.getLanguageCode() == null ? Language.getDefaultLocale().getLanguage() : user.getLanguageCode();
    }

    public boolean isCommand() {
        return messageType == MessageType.MESSAGE || messageType == MessageType.CALLBACK;
    }

    public boolean isUnsupportedMedia() {
        return !isCommand() && messageType != MessageType.OTHER;
    }

    public String getText() {
        return message;
    }

    public String getUserId() {
        return author.getExternalId();
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
                " type='" + messageType + '\'' +
                ", message='" + message + '\'' +
                ", userId='" + author.getExternalId() + '\'' +
                ", firstName='" + author.getFirstName() + '\'' +
                ", lastName='" + author.getLastName() + '\'' +
                ", username='" + author.getUsername() + '\'' +
                ", languageCode='" + userLanguageCode + '\'' +
                ", isQuery='" + isQuery + '\'' +
                '}';
    }
}
