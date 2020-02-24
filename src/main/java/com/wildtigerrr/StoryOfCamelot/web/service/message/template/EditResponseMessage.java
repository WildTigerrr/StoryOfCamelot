package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

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

    public static class EditResponseMessageBuilder {
        private String targetId;
        public EditResponseMessageBuilder targetId(String targetId) {
            this.targetId = targetId;
            return this;
        }
        public EditResponseMessageBuilder targetId(UpdateWrapper update) {
            this.targetId = update.getUserId();
            return this;
        }
        public EditResponseMessageBuilder targetId(Player player) {
            this.targetId = player.getExternalId();
            return this;
        }

        public EditResponseMessageBuilder messageId(Integer messageId) {
            this.messageId = messageId;
            return this;
        }
        public EditResponseMessageBuilder messageId(UpdateWrapper update) {
            this.messageId = update.getMessageId();
            return this;
        }
    }

}