package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.ActionHandler;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.ValidationResult;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class NicknameCommandHandler extends CommandHandler {

    private final int NICKNAME_MAX_LENGTH = 40;

    private final PlayerService playerService;
    private final ActionHandler actionHandler;

    protected NicknameCommandHandler(ResponseManager messages, TranslationManager translation, PlayerService playerService, ActionHandler actionHandler) {
        super(messages, translation);
        this.playerService = playerService;
        this.actionHandler = actionHandler;
    }

    @Override
    public void process(IncomingMessage message) {
        TextIncomingMessage textMessage = (TextIncomingMessage) message;
        String nickname = textMessage.text().startsWith("/nickname ")
                ? textMessage.text().replaceFirst("/nickname ", "")
                : textMessage.text();
        setNickname(textMessage.getPlayer(), nickname);
    }

    public void sendNicknameChangeRequest(TextIncomingMessage message) {
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(translation.getMessage("player.nickname.request", message))
                .build()
        );
    }

    public void setNickname(Player player, String newName) {
        NicknameValidator validator = new NicknameValidator(player.getLanguage(), newName);
        ValidationResult result = validator.validate();
        String message = result.getFailMessage();
        if (result.success()) {
            player.setNickname(newName);
            playerService.update(player);
            message = translation.getMessage("player.nickname.accept", player,
                    new Object[]{player.getNickname()});
        }
        messages.sendMessage(TextResponseMessage.builder().by(player)
                .text(message).applyMarkup(true).build()
        );
        if (result.success()) actionHandler.sendAvailableActions(player);
    }

    public int getNicknameMaxLength() {
        return this.NICKNAME_MAX_LENGTH;
    }

    class NicknameValidator {

        Language language;
        String newNickname;

        NicknameValidator(Language language, String nickname) {
            this.language = language;
            this.newNickname = nickname;
        }

        ValidationResult validate() {
            ValidationResult result = ValidationResult.get();
            if (newNickname == null || newNickname.isBlank()) {
                result.setFailMessage(translation.getMessage("player.nickname.empty", language));
            } else if (containsSpecialCharacters()) {
                result.setFailMessage(translation.getMessage("player.nickname.wrong-symbols", language));
            } else if (playerService.findByNickname(newNickname) != null) {
                result.setFailMessage(translation.getMessage("player.nickname.duplicate", language, new Object[]{newNickname}));
            } else if (newNickname.length() > getNicknameLengthMax()) {
                result.setFailMessage(translation.getMessage("player.nickname.too-long", language,
                        new Object[]{String.valueOf(getNicknameLengthMax())}));
            }
            return result;
        }

        boolean containsSpecialCharacters() {
            String updated = newNickname.replaceAll(" {2,}", " ").replaceAll("[*_`\\\\/]", "");
            return !newNickname.equals(updated);
        }

        int getNicknameLengthMax() {
            return NICKNAME_MAX_LENGTH;
        }

    }

}
