package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActionHandler {

    private final CacheProvider cacheService;
    private final ResponseManager messages;
    private final TranslationManager translation;

    public ActionHandler(CacheProvider cacheService, ResponseManager messages, TranslationManager translation) {
        this.cacheService = cacheService;
        this.messages = messages;
        this.translation = translation;
    }

    public void sendAvailableActions(Player player) {
        List<ReplyButton> buttons = getAvailableActions(player);
        messages.sendMessage(TextResponseMessage.builder().by(player)
                .text(translation.getMessage("commands.available-action", player))
                .keyboard(KeyboardManager.getReplyByButtons(buttons, player.getLanguage()))
                .applyMarkup(true).build()
        );
    }

    public ReplyKeyboardMarkup getAvailableActionsKeyboard(Player player) {
        return KeyboardManager.getReplyByButtons(getAvailableActions(player), player.getLanguage());
    }

    public List<ReplyButton> getAvailableActions(Player player) {
        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, player.getId());
        if (state.isMoving()) {
            return new ArrayList<>() {{
                add(ReplyButton.ME);
                add(ReplyButton.BACKPACK);
            }};
        }
        if (state.hasEnemy()) {
            return new ArrayList<>() {{
                add(ReplyButton.FIGHT);
                add(ReplyButton.SKIP_LINE);
                add(ReplyButton.ME);
                add(ReplyButton.BACKPACK);
            }};
        }
        return getLocationActionsKeyboard(player.getLocation());
    }

    public List<ReplyButton> getLocationActionsKeyboard(Location location) {
        List<ReplyButton> buttons = new ArrayList<>();
        buttons.add(ReplyButton.ME);
        buttons.add(ReplyButton.MOVE);
        buttons.add(ReplyButton.BACKPACK);
        buttons.add(ReplyButton.SEARCH_ENEMIES);
        if (location.getHasEnemies()) {
            buttons.add(ReplyButton.SEARCH_ENEMIES);
        }
        if (location.getHasStores()) {
            buttons.add(ReplyButton.STORES);
        }
        return buttons;
    }

    public List<ReplyButton> getPlayerInfoActions(Player player) {
        return new ArrayList<>() {{
            add(ReplyButton.BACK);
            add(ReplyButton.SKILLS);
            add(ReplyButton.BACKPACK);
            add(ReplyButton.PLAYERS_TOP);
        }};
    }

}
