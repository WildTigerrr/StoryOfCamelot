package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Builder
@Getter
public class TextResponseMessage implements ResponseMessage {

    @Builder.Default
    private final ResponseType type = ResponseType.TEXT;
    @NonNull
    private final String targetId;
    private final String text;
    @Builder.Default
    private final boolean applyMarkup = false;
    private final ReplyKeyboard keyboard;

    public static class TextResponseMessageBuilder {
        private String targetId;
        public TextResponseMessageBuilder targetId(String targetId) {
            this.targetId = targetId;
            return this;
        }
        public TextResponseMessageBuilder targetId(UpdateWrapper update) {
            this.targetId = update.getUserId();
            return this;
        }
        public TextResponseMessageBuilder targetId(Player player) {
            this.targetId = player.getExternalId();
            return this;
        }
    }

}
