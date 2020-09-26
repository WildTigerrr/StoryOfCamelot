package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.EditResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

@Service
public class LanguageCommandHandler extends TextMessageHandler {

    // TODO After language selected check for nickname update needed -> Subscribe for actions. By QuestHandler?

    private final PlayerService playerService;
    private final NicknameCommandHandler nicknameCommandHandler;

    public LanguageCommandHandler(ResponseManager messages, TranslationManager translation, PlayerService playerService, NicknameCommandHandler nicknameCommandHandler) {
        super(messages, translation);
        this.playerService = playerService;
        this.nicknameCommandHandler = nicknameCommandHandler;
    }

    @Override
    public void process(IncomingMessage message) {
        setLanguage((TextIncomingMessage) message);
    }

    public void sendLanguageSelector(String userId, Language lang) {
        messages.sendMessage(TextResponseMessage.builder().lang(lang)
                .text(translation.getMessage("tutorial.lang.choose", lang))
                .keyboard(KeyboardManager.getKeyboardForLanguageSelect())
                .targetId(userId).build()
        );
    }

    private void setLanguage(TextIncomingMessage message) {
        if (message.getParsedCommand().hasExtraParams()) {
            String langCode = message.getParsedCommand().paramByNum(1);
            if (Language.isValidLanguageCode(langCode)) {
                Player player = message.getPlayer();
                player.setLanguage(Language.values()[Integer.parseInt(langCode)]);
                playerService.update(player);
                sendLanguageSelectedMessage(message);
                if (player.getNickname().equals(player.getExternalId())) {
                    nicknameCommandHandler.sendNicknameChangeRequest(message);
                }
            } else {
                messages.sendMessage(TextResponseMessage.builder().by(message)
                        .text(translation.getMessage("commands.invalid", message)).build()
                );
            }
        } else {
            Language lang = message.getPlayer().getLanguage() == null
                    ? Language.byCountryCode(message.getAuthor().getLanguageCode())
                    : message.getPlayer().getLanguage();
            sendLanguageSelector(message.getUserId(), lang);
        }
    }

    private void sendLanguageSelectedMessage(IncomingMessage message) {
        String text = translation.getMessage("tutorial.lang.selected",
                message.getPlayer(),
                new Object[]{message.getPlayer().getLanguage().getName()}
        );
        if (message.isQuery()) {
            messages.sendMessage(EditResponseMessage.builder().by(message)
                    .text(text).applyMarkup(true).build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(text).applyMarkup(true).build()
            );
        }
    }

}
