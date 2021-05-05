package com.wildtigerrr.StoryOfCamelot.database.redis.schema;

import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.Fighter;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EnemyState {

    private Integer hitpoints;
    private Integer endurance;

    public EnemyState(Integer hitpoints, Integer endurance) {
        this.hitpoints = hitpoints;
        this.endurance = endurance;
    }

    public static EnemyState of(Integer hitpoints, Integer endurance) {
        return new EnemyState(hitpoints, endurance);
    }

    public static EnemyState of(Fighter fighter) {
        return new EnemyState(fighter.getHealth(), 0);
    }

}
