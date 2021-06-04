package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.base.BattleLog;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.player.ExperienceService;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Skill;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
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
    private final ExperienceService experienceService;
    private final Random random = new Random();

    @Autowired
    public BattleHandler(TranslationManager translation, ExperienceService experienceService) {
        this.translation = translation;
        this.experienceService = experienceService;
    }

    public BattleLog fight(Fighter attacker, Fighter defender, Language lang) {
        ArrayList<String> battleLog = new ArrayList<>();
        battleLog.add(translation.getMessage("battle.log.start", lang,
                new Object[]{attacker.getName(lang), defender.getName(lang)}));
        battleLog.add(translation.getMessage("battle.log.fight", lang));
        while (attacker.isAlive() && defender.isAlive()) {
            applyDamageAndLog(attacker, defender, lang, battleLog);
            if (defender.isAlive())
                applyDamageAndLog(defender, attacker, lang, battleLog);
        }
        battleLog.add("\n");
        if (attacker.isAlive()) {
            battleLog.add(translation.getMessage("battle.log.winner", lang,
                    new Object[]{attacker.getName(lang)}));
        } else {
            battleLog.add(translation.getMessage("battle.log.winner", lang,
                    new Object[]{defender.getName(lang)}));
        }
        return new BattleLog(
                attacker.getId(),
                defender.getId(),
                defender.getType(),
                attacker.isAlive(),
                true,
                battleLog
        );
    }

    public BattleLog fightDynamic(Player attacker, Fighter defender, Language lang, BattleLog battleLog, Skill skill) {
        applyDamageAndLog(attacker, defender, lang, battleLog.getLog(), skill);
        if (defender.isAlive())
            applyDamageAndLog(defender, attacker, lang, battleLog.getLog());

        if (!defender.isAlive()) {
            battleLog.getLog().add("\n");
            battleLog.getLog().add(translation.getMessage("battle.log.winner", lang,
                    new Object[]{attacker.getName(lang)}));
            battleLog = new BattleLog(battleLog, true, true);
        } else if (!attacker.isAlive()) {
            battleLog.getLog().add("\n");
            battleLog.getLog().add(translation.getMessage("battle.log.winner", lang,
                    new Object[]{defender.getName(lang)}));
            battleLog = new BattleLog(battleLog, false, true);
        }
        return battleLog;
    }

    private void applyDamageAndLog(Fighter attacker, Fighter defender, Language lang, List<String> log, Skill skill) {
        boolean isCrit = isCrit();
        int damage = calculateDamage(skill == null ? attacker.getDamage() : skill.calculateStrength((Player) attacker), defender.getDefence(), isCrit);
        if (attacker instanceof Player) {
            experienceService.addExperience((Player) attacker, Stats.STRENGTH, damage * EXPERIENCE_DAMAGE_DEALT_MULTIPLIER, EXPERIENCE__SEND_DAMAGE_DEALT);
        }
        String messageTemplate = isCrit ? "battle.log.row-crit" : "battle.log.row";
        log.add(translation.getMessage(messageTemplate, lang, new Object[]{
                attacker.getName(lang), attacker.getHealth(), defender.getName(lang), defender.getHealth(), damage
        }));
        defender.applyDamage(damage);
        if (defender instanceof Player) {
            experienceService.addExperience((Player) defender, Stats.HEALTH, damage * EXPERIENCE_DAMAGE_RECEIVED_MULTIPLIER, EXPERIENCE__SEND_DAMAGE_RECEIVED);
        }
    }

    private void applyDamageAndLog(Fighter attacker, Fighter defender, Language lang, List<String> log) {
        applyDamageAndLog(attacker, defender, lang, log, null);
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