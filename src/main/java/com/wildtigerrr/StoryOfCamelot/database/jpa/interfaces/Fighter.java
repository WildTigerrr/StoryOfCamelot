package com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces;

import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;

public interface Fighter {

    String getId();
    int getDamage();
    int getDefence();
    int getHealth();
    String getName(Language lang);
    boolean isAlive();
    void applyDamage(int damage);
    EnemyType getType();

}
