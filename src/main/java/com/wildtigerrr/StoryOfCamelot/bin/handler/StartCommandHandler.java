package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.LanguageService;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StartCommandHandler extends TextMessageHandler {

    private final LanguageCommandHandler languageCommandHandler;
    private final NicknameCommandHandler nicknameCommandHandler;

    public StartCommandHandler(ResponseManager messages, TranslationManager translation, LanguageCommandHandler languageCommandHandler, NicknameCommandHandler nicknameCommandHandler) {
        super(messages, translation);
        this.languageCommandHandler = languageCommandHandler;
        this.nicknameCommandHandler = nicknameCommandHandler;
    }

    @Override
    public void process(IncomingMessage message) {
        Player player = message.getPlayer();
        // TODO Not set default language
        if (player.getLanguage() == null) {
            Language lang = Language.byCountryCode(message.getAuthor().getLanguageCode());
            languageCommandHandler.sendLanguageSelector(message.getUserId(), lang);
        } else if (player.getNickname().equals(player.getExternalId())) {
            nicknameCommandHandler.process(message);
        } else {
            sendAvailableActions(player);
        }
    }

    private void sendAvailableActions(Player player) {
        // TODO Action Service (?) for pulling available actions
        List<ReplyButton> buttons = new ArrayList<>() {
            {
                add(ReplyButton.ME);
                add(ReplyButton.MOVE);
            }
        };
        messages.sendMessage(TextResponseMessage.builder().by(player)
                .keyboard(KeyboardManager.getReplyByButtons(buttons, player.getLanguage()))
                .applyMarkup(true).build()
        );
    }

}
