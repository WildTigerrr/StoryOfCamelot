package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ImageIncomingMessage extends IncomingMessage {

    private final List<IncomingImage> images = new ArrayList<>();

    public ImageIncomingMessage(Update update) {
        super(update);

        List<PhotoSize> photos = update.getMessage().getPhoto();
        if (!photos.isEmpty()) images.addAll(photos.stream().map(IncomingImage::new).collect(Collectors.toList()));
    }

    public String getBiggetImageId() {
        IncomingImage image = images.stream().max(Comparator.comparing(IncomingImage::getFileSize)).orElse(null);
        return image != null ? image.getFileId() : "";
    }

    public String getAuthorCaption() {
        return getText() != null ? getText() + ", " + getAuthor().toString() : getAuthor().toString();
    }

    @Getter
    class IncomingImage {
        String fileId;
        int fileSize;

        public IncomingImage(String fileId, int fileSize) {
            this.fileId = fileId;
            this.fileSize = fileSize;
        }

        public IncomingImage(PhotoSize photo) {
            this.fileId = photo.getFileId();
            this.fileSize = photo.getFileSize();
        }
    }

}
