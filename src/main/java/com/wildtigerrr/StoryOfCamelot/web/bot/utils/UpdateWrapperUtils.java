package com.wildtigerrr.StoryOfCamelot.web.bot.utils;

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

}