package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import lombok.Builder;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Builder
@Getter
public class TextResponseMessage implements ResponseMessage {

    private static final String DEFAULT_TARGET_ID = BotConfig.ADMIN_CHANNEL_ID;

    @Builder.Default
    private final ResponseType type = ResponseType.TEXT;
    private final String targetId;
    private final String text;
    @Builder.Default
    private final boolean applyMarkup = false;
    @Builder.Default
    private final boolean forceReply = false;
    private final ReplyKeyboard keyboard;

    public ReplyKeyboard getKeyboard() {
        return keyboard != null
                ? keyboard
                : forceReply ? new ForceReplyKeyboard() : null;
    }

    public static class TextResponseMessageBuilder {
        private String targetId;
        public TextResponseMessageBuilder targetId(String targetId) {
            this.targetId = targetId == null ? DEFAULT_TARGET_ID : targetId;
            return this;
        }
        public TextResponseMessageBuilder targetId(UpdateWrapper update) {
            this.targetId = update.getUserId() == null ? DEFAULT_TARGET_ID : update.getUserId();
            return this;
        }
        public TextResponseMessageBuilder targetId(IncomingMessage message) {
            this.targetId = message.getUserId() == null ? DEFAULT_TARGET_ID : message.getUserId();
            return this;
        }
        public TextResponseMessageBuilder targetId(Player player) {
            this.targetId = player.getExternalId() == null ? DEFAULT_TARGET_ID : player.getExternalId();
            return this;
        }
    }

}
