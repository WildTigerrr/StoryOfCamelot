package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.CharacterStatus;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActionHandler {

    private final CacheProvider cacheService;

    public ActionHandler(CacheProvider cacheService) {
        this.cacheService = cacheService;
    }

    public List<ReplyButton> getAvailableActions(Player player) {
        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, player.getId());
        if (state.isMoving()) {
            if (player.getLocation().getHasEnemies()) {
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
        } else if (state.hasEnemy()) {
            return new ArrayList<>() {{
                add(ReplyButton.ME);
                add(ReplyButton.FIGHT);
            }};
        }

        return new ArrayList<>() {{
            add(ReplyButton.ME);
        }};
    }

}
