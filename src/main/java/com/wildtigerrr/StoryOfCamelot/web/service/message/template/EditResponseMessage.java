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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Builder
@Getter
public class EditResponseMessage implements ResponseMessage {

    @Builder.Default
    private final ResponseType type = ResponseType.EDIT;
    @NonNull
    private final String targetId;
    @NonNull
    private final Integer messageId;
    private final String text;
    @Builder.Default
    private final boolean applyMarkup = false;
    private final InlineKeyboardMarkup keyboard;
    @NonNull
    private final Language lang;

    @Override
    public Language getLanguage() {
        return lang;
    }

    public static class EditResponseMessageBuilder {
        public EditResponseMessageBuilder targetId(String targetId) {
            this.targetId = targetId;
            return this;
        }
        public EditResponseMessageBuilder targetId(UpdateWrapper update) {
            return this.targetId(update.getUserId());
        }
        public EditResponseMessageBuilder targetId(IncomingMessage message) {
            return this.targetId(message.getUserId());
        }
        public EditResponseMessageBuilder targetId(Player player) {
            return this.targetId(player.getExternalId());
        }

        public EditResponseMessageBuilder messageId(Integer messageId) {
            this.messageId = messageId;
            return this;
        }
        public EditResponseMessageBuilder messageId(UpdateWrapper update) {
            return this.messageId(update.getMessageId());
        }
        public EditResponseMessageBuilder messageId(IncomingMessage message) {
            return this.messageId(message.getMessageId());
        }

        public EditResponseMessageBuilder lang(Language language) {
            this.lang = language;
            return this;
        }
        public EditResponseMessageBuilder lang(Player player) {
            return this.lang(player.getLanguage());
        }
        public EditResponseMessageBuilder lang(IncomingMessage message) {
            return this.lang(message.getPlayer());
        }
    }

}
