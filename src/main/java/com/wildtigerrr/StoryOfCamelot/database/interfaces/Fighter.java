package com.wildtigerrr.StoryOfCamelot.database.interfaces;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;

public interface Fighter {

    int getDamage();
    int getDefence();
    int getHealth();
    String getName(Language lang);
    boolean isAlive();
    void applyDamage(int damage);
    PlayerState.EnemyType getType();

}
