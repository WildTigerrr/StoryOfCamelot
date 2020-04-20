package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DiceResponseMessage implements ResponseMessage {

    @Builder.Default
    private final ResponseType type = ResponseType.DICE;
    private final String targetId;

    private final DiceIncomingMessage incomingMessage;

    @Override
    public String getText() {
        return null;
    }

    @Override
    public boolean isApplyMarkup() {
        return false;
    }

    public static class DiceResponseMessageBuilder {
        private String targetId;
        public DiceResponseMessageBuilder targetId(String targetId) {
            this.targetId = targetId;
            return this;
        }
        public DiceResponseMessageBuilder targetId(UpdateWrapper update) {
            this.targetId = update.getUserId();
            return this;
        }
        public DiceResponseMessageBuilder targetId(IncomingMessage message) {
            this.targetId = message.getUserId();
            return this;
        }
        public DiceResponseMessageBuilder targetId(Player player) {
            this.targetId = player.getExternalId();
            return this;
        }
    }

}
