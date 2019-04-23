package com.wildtigerrr.StoryOfCamelot.bin;

import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import org.springframework.stereotype.Service;

@Service
public class BattleHandler {

    public Integer calculateDamage(Integer attack, Integer defence, Boolean isCritical) {
        int battleRandom = BotConfig.BATTLE_RANDOM;
        int randomAttack = attack + (int)(Math.random() * ((attack/battleRandom) * 2  + 1) - (attack/battleRandom));
        int randomDefence = defence + (int)(Math.random() * ((defence/battleRandom) * 2  + 1) - (defence/battleRandom));
        int damage;
        if (isCritical) {
            damage = (int) (((double) randomAttack)*1.5 - ((double) randomDefence) / 4.5);
        } else {
            damage =  randomAttack - (randomDefence / 3);
        }
        if (damage <= 0) {
            damage = isCritical ? 2 : 1;
        }
        return damage;
    }

}