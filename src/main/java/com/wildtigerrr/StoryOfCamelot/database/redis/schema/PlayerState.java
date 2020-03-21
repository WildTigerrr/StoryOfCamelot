package com.wildtigerrr.StoryOfCamelot.database.redis.schema;

import com.wildtigerrr.StoryOfCamelot.database.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidFighterException;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PlayerState implements Serializable {

    private Integer id;
    private PlayerStatus status;
    private Enemy enemy;

    public PlayerState(Player player, Fighter fighter) {
        this.id = player.getId();
        this.status = player.getStatus();
        this.enemy = enemyOf(fighter);
    }

    private Enemy enemyOf(Fighter fighter) {
        switch (fighter.getType()) {
            case PLAYER: return enemyOf((Player) fighter);
            case MOB: return enemyOf((Mob) fighter);
            default: throw new InvalidFighterException(String.valueOf(fighter.getClass()));
        }
    }

    private Enemy enemyOf(Player player) {
        return new Enemy(player.getId(), EnemyType.PLAYER);
    }

    private Enemy enemyOf(Mob mob) {
        return new Enemy(mob.getId(), EnemyType.MOB);
    }

    @Getter
    public class Enemy implements Serializable {
        private Integer id;
        private EnemyType type;

        private Enemy(Integer id, EnemyType type) {
            this.id = id;
            this.type = type;
        }
    }

    public enum EnemyType {
        PLAYER,
        MOB
    }

}
