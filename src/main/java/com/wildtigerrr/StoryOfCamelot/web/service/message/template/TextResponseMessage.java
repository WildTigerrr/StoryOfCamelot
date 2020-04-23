package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Builder
@Getter
public class TextResponseMessage implements ResponseMessage {

    private static final String DEFAULT_TARGET_ID = BotConfig.ADMIN_CHANNEL_ID;

    @Builder.Default
    private final ResponseType type = ResponseType.TEXT;
    @NonNull
    private final String targetId;
    @NonNull
    private final String text;
    @Builder.Default
    private final boolean applyMarkup = false;
    @Builder.Default
    private final boolean forceReply = false;
    private final ReplyKeyboard keyboard;
    @NonNull
    private final Language lang;

    public ReplyKeyboard getKeyboard() {
        return keyboard != null
                ? keyboard
                : forceReply ? new ForceReplyKeyboard() : null;
    }

    @Override
    public Language getLanguage() {
        return lang;
    }

    public static class TextResponseMessageBuilder {
        public TextResponseMessageBuilder targetId(String targetId) {
            this.targetId = targetId == null ? DEFAULT_TARGET_ID : targetId;
            return this;
        }
        public TextResponseMessageBuilder targetId(UpdateWrapper update) {
            return this.targetId(update.getUserId());
        }
        public TextResponseMessageBuilder targetId(IncomingMessage message) {
            return this.targetId(message.getUserId());
        }
        public TextResponseMessageBuilder targetId(Player player) {
            return this.targetId(player.getExternalId());
        }

        public TextResponseMessageBuilder lang(Language language) {
            this.lang = language;
            return this;
        }
        public TextResponseMessageBuilder lang(IncomingMessage message) {
            return this.lang(message.getPlayer());
        }
        public TextResponseMessageBuilder lang(Player player) {
            return this.lang(player.getLanguage());
        }
    }

}
