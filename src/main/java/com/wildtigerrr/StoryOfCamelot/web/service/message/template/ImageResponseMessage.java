package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
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
    @NonNull
    private final Language lang;

    @Override
    public Language getLanguage() {
        return lang;
    }

    @Override
    public String getText() {
        return caption.isBlank() ? "Image without text" : caption;
    }

    @Override
    public boolean isApplyMarkup() {
        return false;
    }

    public static class ImageResponseMessageBuilder {
        /**
         * Set default params from Player instance<br/>
         * Params to set:<br/>
         * <ul>
         *     <li>targetId</li>
         *     <li>lang</li>
         * </ul>
         *
         * @param message - IncomingMessage instance to get params from
         * @return Builder
         */
        public ImageResponseMessageBuilder by(IncomingMessage message) {
            targetId(message)
                    .lang(message);
            return this;
        }

        /**
         * Set default params from Player instance<br/>
         * Params to set:<br/>
         * <ul>
         *     <li>targetId</li>
         *     <li>lang</li>
         * </ul>
         *
         * @param player - Player instance to get params from
         * @return Builder
         */
        public ImageResponseMessageBuilder by(Player player) {
            targetId(player)
                    .lang(player);
            return this;
        }

        public ImageResponseMessageBuilder targetId(String targetId) {
            this.targetId = targetId;
            return this;
        }
        public ImageResponseMessageBuilder targetId(UpdateWrapper update) {
            return this.targetId(update.getUserId());
        }
        public ImageResponseMessageBuilder targetId(IncomingMessage message) {
            return this.targetId(message.getUserId());
        }
        public ImageResponseMessageBuilder targetId(Player player) {
            return this.targetId(player.getExternalId());
        }

        public ImageResponseMessageBuilder lang(Language language) {
            this.lang = language;
            return this;
        }
        public ImageResponseMessageBuilder lang(Player player) {
            return this.lang(player.getLanguage());
        }
        public ImageResponseMessageBuilder lang(IncomingMessage message) {
            return this.lang(message.getPlayer());
        }
    }

}
