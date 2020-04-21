package com.wildtigerrr.StoryOfCamelot.web.service.message;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.Author;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.MessageType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.DiceIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.ImageIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.StickerIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Log4j2
public class IncomingMessage {

    @Getter(AccessLevel.NONE)
    private final String text;
    private final MessageType messageType;
    private final Author author;
    private final boolean isQuery;
    private final Long chatId;
    private final int messageId;
    private final String queryId;
    private final long startTime;

    private Player player;
    private Command command;

    public static IncomingMessage from(Update update) {
        MessageType type = IncomingMessageUtils.defineUpdateType(update);
        switch (type) {
            case MESSAGE:
            case CALLBACK:
                return new TextIncomingMessage(update);
            case PHOTO:
                return new ImageIncomingMessage(update);
            case STICKER:
                return new StickerIncomingMessage(update);
            case DICE:
                return new DiceIncomingMessage(update);
            default:
                return new IncomingMessage(update);
        }
    }

    public IncomingMessage(Update update) {
        this.startTime = System.currentTimeMillis();
        this.isQuery = update.hasCallbackQuery();
        this.messageType = IncomingMessageUtils.defineUpdateType(update);
        this.author = IncomingMessageUtils.getUpdateAuthor(update);
        if (isQuery) this.author.setExternalId(update.getCallbackQuery().getMessage().getChatId().toString());
        this.text = IncomingMessageUtils.getUpdateMessageText(update, isCommand(), hasCaption());
        this.chatId = isQuery ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
        this.messageId = isQuery ? update.getCallbackQuery().getMessage().getMessageId() : update.getMessage().getMessageId();
        this.queryId = isQuery ? update.getCallbackQuery().getId() : null;
    }

    public void logFinish() {
        log.info(senderLog() + ": " + text() + " - Finished in " + elapsedTime() + "ms");
    }

    public String text() {
        return this.text;
    }

    public boolean isCommand() {
        return this.messageType == MessageType.MESSAGE || messageType == MessageType.CALLBACK;
    }

    public boolean hasCaption() {
        return this.messageType == MessageType.PHOTO;
    }

    public String getUserId() {
        return this.author.getExternalId();
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.author.setId(player.getId());
    }

    public Command getCommand() {
        if (this.command == null) this.command = IncomingMessageUtils.fetchCommandFromMessage(this.text, this.player.getLanguage());
        return this.command;
    }

    public String senderLog() {
        return this.player.getNickname() + "["
                + this.player.getExternalId() + "/"
                + this.player.getId()
                + (this.author.getUsername() != null ? "/@" + this.author.getUsername() : "")
                + "]";
    }

    public long elapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public String toString() {
        return "IncomingMessage{" +
                " type='" + messageType + '\'' +
                ", message='" + text + '\'' +
                ", userId='" + author.getExternalId() + '\'' +
                ", firstName='" + author.getFirstName() + '\'' +
                ", lastName='" + author.getLastName() + '\'' +
                ", username='" + author.getUsername() + '\'' +
                ", languageCode='" + author.getLanguageCode() + '\'' +
                ", isQuery='" + isQuery + '\'' +
                '}';
    }

}
