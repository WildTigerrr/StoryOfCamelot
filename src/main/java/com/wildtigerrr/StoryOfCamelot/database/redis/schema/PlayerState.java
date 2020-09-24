package com.wildtigerrr.StoryOfCamelot.database.redis.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.BattleLog;
import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.CharacterStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.UserStatus;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidFighterException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("PlayerState")
@Getter
public class PlayerState implements Serializable {

    private String id;
    private CharacterStatus status;
    @Setter
    private UserStatus userStatus;
    @Setter
    private Enemy enemy;
    @Setter
    private BattleLog lastBattle;

    public PlayerState(Player player, Fighter fighter) {
        this.id = player.getId();
        this.status = player.getStatus();
        this.enemy = enemyOf(fighter);
    }

    public void ban() {
        this.userStatus = userStatus.ifBan();
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
