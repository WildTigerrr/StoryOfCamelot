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

@Builder
@Getter
public class DiceResponseMessage implements ResponseMessage {

    @Builder.Default
    private final ResponseType type = ResponseType.DICE;
    private final String targetId;
    @NonNull
    private final Language lang;
    private final String emoji;

    private final DiceIncomingMessage incomingMessage;

    @Override
    public Language getLanguage() {
        return lang;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public boolean isApplyMarkup() {
        return false;
    }

    public static class DiceResponseMessageBuilder {
        public DiceResponseMessageBuilder targetId(String targetId) {
            this.targetId = targetId;
            return this;
        }
        public DiceResponseMessageBuilder targetId(UpdateWrapper update) {
            return this.targetId(update.getUserId());
        }
        public DiceResponseMessageBuilder targetId(IncomingMessage message) {
            return this.targetId(message.getUserId());
        }
        public DiceResponseMessageBuilder targetId(Player player) {
            return this.targetId(player.getExternalId());
        }

        public DiceResponseMessageBuilder lang(Language language) {
            this.lang = language;
            return this;
        }
        public DiceResponseMessageBuilder lang(IncomingMessage message) {
            return this.lang(message.getPlayer());
        }
        public DiceResponseMessageBuilder lang(Player player) {
            return this.lang(player.getLanguage());
        }
    }

}
