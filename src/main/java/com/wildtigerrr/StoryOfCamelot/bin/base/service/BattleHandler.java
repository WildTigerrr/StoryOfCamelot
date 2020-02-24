package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.wildtigerrr.StoryOfCamelot.web.BotConfig.*;

@Log4j2
@Service
public class BattleHandler {

    private final TranslationManager translation;
    private final Random random = new Random();

    @Autowired
    public BattleHandler(TranslationManager translation) {
        this.translation = translation;
    }

    public List<String> fight(Fighter attacker, Fighter defender, Language lang) {
        List<String> battleLog = new ArrayList<>();
        battleLog.add(translation.getMessage("battle.log.start", lang,
                new Object[]{attacker.getName(lang), defender.getName(lang)}));
        battleLog.add(translation.getMessage("battle.log.fight", lang));
        while (attacker.isAlive() && defender.isAlive()) {
            applyDamageAndLog(attacker, defender, lang, battleLog);
            if (defender.isAlive())
                applyDamageAndLog(defender, attacker, lang, battleLog);
        }
        if (attacker.isAlive()) {
            battleLog.add(translation.getMessage("battle.log.winner", lang,
                    new Object[]{attacker.getName(lang)}));
        } else {
            battleLog.add(translation.getMessage("battle.log.winner", lang,
                    new Object[]{defender.getName(lang)}));
        }
        return battleLog;
    }

    private void applyDamageAndLog(Fighter attacker, Fighter defender, Language lang, List<String> log) {
        int damage = calculateDamage(attacker.getDamage(), defender.getDefence(), isCrit());
        log.add(translation.getMessage("battle.log.row", lang, new Object[]{
                attacker.getName(lang), attacker.getHealth(), defender.getName(lang), defender.getHealth(), damage
        }));
        defender.applyDamage(damage);
    }

    private int calculateDamage(float attack, float armor, boolean isCrit) {
        int att = (int) calculateAttackValue(attack, isCrit);
        int def = (int) calculateArmorValue(armor, isCrit);
        int finalDamage = att - def;
        if (finalDamage <= 0) {
            finalDamage = isCrit ? 2 : 1;
        }
        if (isCrit) {
            log.trace("Critical attack!");
        }
        log.trace("Damage by attack " + attack + " is " + att + ". Defence with armor " + armor + " is " + def + ". Result: " + finalDamage);
        return finalDamage;
    }

    private boolean isCrit() {
        // TODO Crit calculation
        return random.nextInt(100) > 90;
    }

    private float calculateAttackValue(float attack, boolean isCrit) {
        final float cleanAttack = applyDamageRandom(attack);
        return isCrit ? cleanAttack * CRIT_MULTIPLIER : cleanAttack;
    }

    private float calculateArmorValue(float armor, boolean isCrit) {
        final float totalArmor = applyDamageRandom(armor);
        return isCrit ?  (totalArmor  * getPercentage (ARMOR_EFFECTIVENESS * CRIT_MULTIPLIER))
                : (totalArmor * getPercentage(ARMOR_EFFECTIVENESS));
    }

    private float applyDamageRandom(float basicValue) {
        return basicValue + (basicValue * getPercentage (random.nextInt(2 * DAMAGE_RANDOM) - DAMAGE_RANDOM));
    }

    private float getPercentage(float value) {
        return value / 100.0f;
    }

}