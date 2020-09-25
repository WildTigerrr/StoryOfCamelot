package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.ActionHandler;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PlayerCommandHandler extends TextMessageHandler {

    private final ActionHandler actionHandler;
    private final PlayerService playerService;

    public PlayerCommandHandler(ResponseManager messages, TranslationManager translation, ActionHandler actionHandler, PlayerService playerService) {
        super(messages, translation);
        this.actionHandler = actionHandler;
        this.playerService = playerService;
    }

    @Override
    public void process(IncomingMessage message) {
        switch (message.getCommand()) {
            case ME: sendPlayerInfo(message); break;
            case PLAYERS_TOP: sendTopPlayers(message); break;
        }
    }

    private void sendPlayerInfo(IncomingMessage message) {
        log.warn(message.getPlayer().toString());
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .keyboard(KeyboardManager.getReplyByButtons(actionHandler.getPlayerInfoActions(message.getPlayer()), message.getPlayer().getLanguage()))
                .text(message.getPlayer().toString())
                .applyMarkup(true).build()
        );
    }

    private void sendTopPlayers(IncomingMessage message) {
        List<Player> players = playerService.getTopPlayers(10);
        AtomicInteger index = new AtomicInteger();
        String top = "Топ игроков: \n\n" + // TODO Translate
                players.stream()
                        .map(pl -> pl.toStatString(index.incrementAndGet()))
                        .collect(Collectors.joining());
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(top).build()
        );
    }

}
