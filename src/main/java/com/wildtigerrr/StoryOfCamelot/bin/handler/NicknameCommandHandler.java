package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.ValidationResult;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

@Service
public class NicknameCommandHandler extends CommandHandler {

    private final int NICKNAME_MAX_LENGTH = 40;

    private final PlayerService playerService;

    protected NicknameCommandHandler(ResponseManager messages, TranslationManager translation, PlayerService playerService) {
        super(messages, translation);
        this.playerService = playerService;
    }

    @Override
    public void process(IncomingMessage message) {
        TextIncomingMessage textMessage = (TextIncomingMessage) message;
        String nickname = textMessage.text().startsWith("/nickname ")
                ? textMessage.getParsedCommand().paramByNum(1)
                : textMessage.text();
        setNickname(textMessage.getPlayer(), nickname);
    }

    public void setNickname(Player player, String newName) {
        NicknameValidator validator = new NicknameValidator(player, newName);
        ValidationResult result = validator.validate();
        String message = result.getFailMessage();
        if (result.success()) {
            playerService.update(player);
            message = translation.getMessage("player.nickname.accept", player,
                    new Object[]{player.getNickname()});
        }
        messages.sendMessage(TextResponseMessage.builder().lang(player)
                .text(message).targetId(player).applyMarkup(true).build()
        );
    }

    class NicknameValidator {

        Player player;
        String newNickname;

        NicknameValidator(Player player, String nickname) {
            this.player = player;
            this.newNickname = nickname;
        }

        ValidationResult validate() {
            ValidationResult result = ValidationResult.get();
            if (newNickname == null || newNickname.isBlank()) {
                result.setFailMessage(translation.getMessage("player.nickname.empty", player));
            } else if (containsSpecialCharacters()) {
                result.setFailMessage(translation.getMessage("player.nickname.wrong-symbols", player));
            } else if (playerService.findByNickname(newNickname) != null) {
                result.setFailMessage(translation.getMessage("player.nickname.duplicate", player, new Object[]{newNickname}));
            } else if (newNickname.length() > getNicknameLengthMax()) {
                result.setFailMessage(translation.getMessage("player.nickname.too-long", player,
                        new Object[]{String.valueOf(getNicknameLengthMax())}));
            } else if (player.getNickname().isEmpty()) {
                result.setFailMessage(translation.getMessage("player.nickname.empty", player));
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
