package com.wildtigerrr.StoryOfCamelot.database.redis.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.BattleLog;
import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import com.wildtigerrr.StoryOfCamelot.database.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidFighterException;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("PlayerState")
@Getter
public class PlayerState implements Serializable {

    private String id;
    private PlayerStatus status;
    private Enemy enemy;
    private BattleLog lastBattle;

    public PlayerState(Player player, Fighter fighter) {
        this.id = player.getId();
        this.status = player.getStatus();
        this.enemy = enemyOf(fighter);
    }

    public BattleLog getLastBattle() {
        return lastBattle;
    }

    public void setLastBattle(BattleLog lastBattle) {
        this.lastBattle = lastBattle;
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
        private String id;
        private EnemyType type;

        private Enemy(String id, EnemyType type) {
            this.id = id;
            this.type = type;
        }
    }

}
