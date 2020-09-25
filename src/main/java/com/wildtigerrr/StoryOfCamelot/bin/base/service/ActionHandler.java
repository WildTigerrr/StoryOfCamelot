package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActionHandler {

    private final CacheProvider cacheService;

    public ActionHandler(CacheProvider cacheService) {
        this.cacheService = cacheService;
    }

    public ReplyKeyboardMarkup getAvailableActionsKeyboard(Player player) {
        return KeyboardManager.getReplyByButtons(getAvailableActions(player), player.getLanguage());
    }

    public List<ReplyButton> getAvailableActions(Player player) {
        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, player.getId());
        if (state.isMoving()) {
            return new ArrayList<>() {{
                add(ReplyButton.ME);
            }};
        }
        if (state.hasEnemy()) {
            return new ArrayList<>() {{
                add(ReplyButton.ME);
                add(ReplyButton.FIGHT);
            }};
        }
        return getLocationActionsKeyboard(player.getLocation());
    }

    public List<ReplyButton> getLocationActionsKeyboard(Location location) {
        if (location.getHasEnemies()) {
            return new ArrayList<>() {{
                add(ReplyButton.ME);
                add(ReplyButton.MOVE);
                add(ReplyButton.SEARCH_ENEMIES);
            }};
        }
        return new ArrayList<>() {{
            add(ReplyButton.ME);
            add(ReplyButton.MOVE);
        }};
    }

    public List<ReplyButton> getPlayerInfoActions(Player player) {
        return new ArrayList<>() {{
            add(ReplyButton.BACK);
            add(ReplyButton.SKILLS);
            add(ReplyButton.PLAYERS_TOP);
        }};
    }

}
