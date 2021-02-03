package com.wildtigerrr.StoryOfCamelot.web.service.message;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.Author;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.MessageType;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@UtilityClass
public class IncomingMessageUtils {

    public MessageType defineUpdateType(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) return MessageType.MESSAGE;
        else if (update.hasCallbackQuery()) return MessageType.CALLBACK;
        else if (update.hasEditedMessage()) return MessageType.MESSAGE_EDIT;
        else if (update.hasMessage() && update.getMessage().hasPhoto()) return MessageType.PHOTO;
        else if (update.hasMessage() && update.getMessage().hasAnimation()) return MessageType.GIF;
        else if (update.hasMessage() && update.getMessage().hasAudio()) return MessageType.AUDIO;
        else if (update.hasMessage() && update.getMessage().hasContact()) return MessageType.CONTACT;
        else if (update.hasMessage() && update.getMessage().hasDocument()) return MessageType.DOCUMENT;
        else if (update.hasMessage() && update.getMessage().hasLocation()) return MessageType.LOCATION;
        else if (update.hasMessage() && update.getMessage().hasSticker()) return MessageType.STICKER;
        else if (update.hasMessage() && update.getMessage().hasVideo()) return MessageType.VIDEO;
        else if (update.hasMessage() && update.getMessage().hasVoice()) return MessageType.VOICE;
        else if (update.hasMessage() && update.getMessage().hasDice()) return MessageType.DICE;
        else return MessageType.OTHER;
    }

    public Author getUpdateAuthor(Update update) {
        User user = update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getFrom()
                : update.getMessage().getFrom();
        return new Author(user);
    }

    public String getUpdateMessageText(Update update, boolean isCommand, boolean hasCaption) {
        String message = "";
        if (isCommand) {
            message = update.hasCallbackQuery() ? update.getCallbackQuery().getData() : StringUtils.escape(update.getMessage().getText().trim());
            if (message.contains("@" + BotConfig.WEBHOOK_USER)) message = message.replace("@" + BotConfig.WEBHOOK_USER, "").trim();
        } else if (hasCaption) {
            message = update.getMessage().getCaption();
        }
        return message;
    }

    public Command fetchCommandFromMessage(String message, Language language) {
        if (message == null || message.length() == 0) return Command.START;
        if (message.startsWith("/")) {
            try {
                String[] commandParts = message.split(" ", 2);
                return Command.valueOf(commandParts[0].substring(1).toUpperCase());
            } catch (IllegalArgumentException e) {
                return Command.START;
            }
        } else {
            return ReplyButton.buttonToCommand(message, language);
        }
    }

}
