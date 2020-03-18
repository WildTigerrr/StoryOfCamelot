package com.wildtigerrr.StoryOfCamelot.database.redis.schema;

import com.wildtigerrr.StoryOfCamelot.database.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PlayerState implements Serializable {

    private Integer id;
    private PlayerStatus status;
//    private Fighter enemy;

    public PlayerState(Player player, Fighter fighter) {
        this.id = player.getId();
        this.status = player.getStatus();
//        this.enemy = fighter;
    }

}
