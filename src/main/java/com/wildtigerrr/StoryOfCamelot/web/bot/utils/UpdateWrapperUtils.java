package com.wildtigerrr.StoryOfCamelot.web.bot.utils;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.Author;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateType;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Comparator;
import java.util.List;

@UtilityClass
public class UpdateWrapperUtils {

    public String getBiggestPhotoId(Update update) {
        List<PhotoSize> photos = update.getMessage().getPhoto();
        if (photos.isEmpty()) return "";
        else if (photos.size() == 1) return photos.get(0).getFileId();
        else return photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).get().getFileId();
    }

    public Command fetchCommandFromMessage(String message, Language language) {
        if (message == null || message.length() == 0) return null;
        if (message.startsWith("/")) {
            try {
                String[] commandParts = message.split(" ", 2);
                return Command.valueOf(commandParts[0].substring(1).toUpperCase());
            } catch (IllegalArgumentException e) {
                if (message.startsWith("/up")) return Command.UP;
                return null;
            }
        } else {
            return ReplyButton.buttonToCommand(message, language);
        }
    }

    public String getUpdateLogCaption(Update update) {
        String authorCaption = UpdateWrapperUtils.getUpdateAuthorCaption(update);
        return update.getMessage().getCaption() != null ?
                update.getMessage().getCaption() + ", " + authorCaption : authorCaption;
    }

    public String getUpdateAuthorCaption(Update update) {
        User user = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getFrom() : update.getMessage().getFrom();
        return new Author(user).toString();
    }

    public UpdateType defineUpdateType(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) return UpdateType.MESSAGE;
        else if (update.hasCallbackQuery()) return UpdateType.CALLBACK;
        else if (update.hasEditedMessage()) return UpdateType.MESSAGE_EDIT;
        else if (update.hasMessage() && update.getMessage().hasPhoto()) return UpdateType.PHOTO;
        else if (update.hasMessage() && update.getMessage().hasAnimation()) return UpdateType.GIF;
        else if (update.hasMessage() && update.getMessage().hasAudio()) return UpdateType.AUDIO;
        else if (update.hasMessage() && update.getMessage().hasContact()) return UpdateType.CONTACT;
        else if (update.hasMessage() && update.getMessage().hasDocument()) return UpdateType.DOCUMENT;
        else if (update.hasMessage() && update.getMessage().hasLocation()) return UpdateType.LOCATION;
        else if (update.hasMessage() && update.getMessage().hasSticker()) return UpdateType.STICKER;
        else if (update.hasMessage() && update.getMessage().hasVideo()) return UpdateType.VIDEO;
        else if (update.hasMessage() && update.getMessage().hasVoice()) return UpdateType.VOICE;
        else return UpdateType.OTHER;
    }

}