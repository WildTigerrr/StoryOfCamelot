package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.io.InputStream;

@Builder
@Getter
public class ImageResponseMessage implements ResponseMessage {

    @Builder.Default
    private final ResponseType type = ResponseType.PHOTO;
    @NonNull
    private final String targetId;
    private final String caption;

    private final File file;
    private final String fileId;
    private final String fileName;
    private final InputStream fileStream;

    @Override
    public String getText() {
        return caption.isBlank() ? "Image without text" : caption;
    }

    @Override
    public boolean isApplyMarkup() {
        return false;
    }

    public static class ImageResponseMessageBuilder {
        private String targetId;
        public ImageResponseMessageBuilder targetId(String targetId) {
            this.targetId = targetId;
            return this;
        }
        public ImageResponseMessageBuilder targetId(UpdateWrapper update) {
            this.targetId = update.getUserId();
            return this;
        }
        public ImageResponseMessageBuilder targetId(Player player) {
            this.targetId = player.getExternalId();
            return this;
        }
    }

}
