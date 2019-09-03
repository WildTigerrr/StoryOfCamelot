package com.wildtigerrr.StoryOfCamelot.database.interfaces;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;

public interface Fighter {

    Integer getDamage();
    Integer getDefence();
    Integer getHealth();
    String getName(Language lang);
    boolean isAlive();
    void applyDamage(int damage);

}
