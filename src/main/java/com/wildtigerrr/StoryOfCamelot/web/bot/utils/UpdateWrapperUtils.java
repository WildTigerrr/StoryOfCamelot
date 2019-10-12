package com.wildtigerrr.StoryOfCamelot.web.bot.utils;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

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

}