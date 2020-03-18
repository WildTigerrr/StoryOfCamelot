package com.wildtigerrr.StoryOfCamelot.database.redis.schema;

import com.wildtigerrr.StoryOfCamelot.database.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import lombok.Getter;

@Getter
public class PlayerState {

    private Integer id;
    private PlayerStatus status;
    private Fighter enemy;

    private PlayerState(Player player, Fighter fighter) {
        this.id = player.getId();
        this.status = player.getStatus();
        this.enemy = fighter;
    }

}
