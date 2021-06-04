package com.wildtigerrr.StoryOfCamelot.database.redis.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.BattleLog;
import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.CharacterStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.UserStatus;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidFighterException;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheTypeObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;

@RedisHash("PlayerState")
@Getter
public class PlayerState implements Serializable, CacheTypeObject {

    private String id;
    private CharacterStatus status;
    @Setter
    private UserStatus userStatus;
    private Enemy enemy;
    @Setter
    private EnemyState enemyState;
    @Setter
    private BattleLog lastBattle;

    public PlayerState(String playerId) {
        this.id = playerId;
        this.status = CharacterStatus.ACTIVE;
        this.userStatus = UserStatus.ACTIVE;
    }

    public PlayerState setEnemy(Fighter fighter) {
        this.enemy = enemyOf(fighter);
        this.status = CharacterStatus.HAS_ENEMY;
        return this;
    }

    public PlayerState(Player player, Fighter fighter) {
        this.id = player.getId();
        this.status = player.getStatus();
        this.enemy = enemyOf(fighter);
    }

    @Override
    public String getKey() {
        return getId();
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

    public boolean isBanned() {
        return userStatus != UserStatus.ACTIVE;
    }

    public boolean isMoving() {
        return status == CharacterStatus.MOVEMENT;
    }

    public boolean hasEnemy() {
        return status == CharacterStatus.HAS_ENEMY;
    }

    public PlayerState initBattleLog(String initialLog) {
        if (getEnemy() == null) return this;
        this.lastBattle = new BattleLog(
                getId(),
                getEnemy().getId(),
                getEnemy().getType(),
                false,
                false,
                new ArrayList<>() {{add(initialLog);}}
        );
        return this;
    }

    public PlayerState ban() {
        this.userStatus = userStatus.ifBan();
        return this;
    }

    public PlayerState move() {
        this.status = CharacterStatus.MOVEMENT;
        return this;
    }

    public PlayerState stop() {
        this.status = CharacterStatus.ACTIVE;
        return this;
    }

    public PlayerState finishBattle() {
        this.status = CharacterStatus.ACTIVE;
        this.enemy = null;
        this.enemyState = null;
        return this;
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
