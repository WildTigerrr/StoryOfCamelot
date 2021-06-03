package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.Map;

@Embeddable
@Getter
@Setter
public class PlayerStats {

    @Setter(AccessLevel.NONE)
    private Integer level;

    private Integer unassignedPoints;

    private Integer strength;
    private Integer health;
    private Integer agility;
    private Integer charisma;
    private Integer intelligence;
    private Integer endurance;
    private Integer luck;

    private Double strengthExp;
    private Double healthExp;
    private Double agilityExp;
    private Double charismaExp;
    private Double intelligenceExp;
    private Double enduranceExp;

    public PlayerStats() {
        level = 1;

        strength = 1;
        health = 1;
        agility = 1;
        charisma = 1;
        intelligence = 1;
        endurance = 1;
        luck = 1;

        strengthExp = 0.0;
        healthExp = 0.0;
        agilityExp = 0.0;
        charismaExp = 0.0;
        intelligenceExp = 0.0;
        enduranceExp = 0.0;
    }

    int getExpToNextStatUp(Integer currentLevel) {
        return (int) Math.pow(currentLevel + 1, 2);
    }

    int getStatsToNextLevelUp() {
        return 5 * level * (level + 1) / 2;
    }

    int getDefaultPoints() {
        return BotConfig.DEFAULT_SKILL_POINTS;
    }

    int getAssignedPoints() {
        return (7 - unassignedPoints + (getDefaultPoints() + (5 * level)));
    }

    public String raiseStat(Stats stat, Integer quantity, Language lang, TranslationManager translation) {
        if (quantity == 0) return "";
        if (quantity > unassignedPoints) return translation.getMessage("player.stats.insufficient-points", lang);
        int newQuantity;
        switch (stat) {
            case STRENGTH:
                strengthExp = 0.0;
                strength += quantity;
                newQuantity = strength;
                break;
            case HEALTH:
                healthExp = 0.0;
                health += quantity;
                newQuantity = health;
                break;
            case AGILITY:
                agilityExp = 0.0;
                agility += quantity;
                newQuantity = agility;
                break;
            case CHARISMA:
                charismaExp = 0.0;
                charisma += quantity;
                newQuantity = charisma;
                break;
            case INTELLIGENCE:
                intelligenceExp = 0.0;
                intelligence += quantity;
                newQuantity = intelligence;
                break;
            case ENDURANCE:
                enduranceExp = 0.0;
                endurance += quantity;
                newQuantity = endurance;
                break;
            case LUCK:
                luck += quantity;
                newQuantity = luck;
                break;
            default:
                return translation.getMessage("player.stats.invalid", lang);
        }
        unassignedPoints -= quantity;
        return translation.getMessage("player.stats.stat-up", lang, new Object[]{stat.whichLowercase(lang), String.valueOf(newQuantity)});
    }

    public ArrayList<String> addStatExp(double exp, Stats stat, Language lang, TranslationManager translation) {
        ArrayList<String> events = new ArrayList<>();
        if (exp == 0) return events;
        boolean up = isStatUp(stat, exp);
        String currentValue;
        while (up) {
            switch (stat) {
                case STRENGTH:
                    strengthExp -= getExpToNextStatUp(strength);
                    strength++;
                    currentValue = String.valueOf(strength);
                    break;
                case HEALTH:
                    healthExp -= getExpToNextStatUp(health);
                    health++;
                    currentValue = String.valueOf(health);
                    break;
                case AGILITY:
                    agilityExp -= getExpToNextStatUp(agility);
                    agility++;
                    currentValue = String.valueOf(agility);
                    break;
                case CHARISMA:
                    charismaExp -= getExpToNextStatUp(charisma);
                    charisma++;
                    currentValue = String.valueOf(charisma);
                    break;
                case INTELLIGENCE:
                    intelligenceExp -= getExpToNextStatUp(intelligence);
                    intelligence++;
                    currentValue = String.valueOf(intelligence);
                    break;
                case ENDURANCE:
                    enduranceExp -= getExpToNextStatUp(endurance);
                    endurance++;
                    currentValue = String.valueOf(endurance);
                    break;
                default:
                    throw new InvalidInputException("Invalid stat: " + stat);
            }
            events.add(statUp(lang, stat.whichLowercase(lang), currentValue, translation)); // MainText.STAT_UP.text(lang, stat.whichLowercase(lang), currentValue)
            if (isLevelUp()) {
                levelUp();
                events.add(translation.getMessage("player.stats.level-up", lang, new Object[]{String.valueOf(getLevel())}));
            }
            up = isStatUp(stat, 0.0);
        }
        return events;
    }

    private String statUp(Language lang, String statName, String value, TranslationManager translation) {
        return translation.getMessage("player.stats.stat-up", lang, new Object[]{statName, value});
    }

    private Boolean isStatUp(Stats stat, Double newExp) {
        return addStatExp(stat, newExp) >= getExpToNextStatUp(getStatValue(stat));
    }

    private Boolean isLevelUp() {
        return getTotalStats() - getAssignedPoints() >= getStatsToNextLevelUp();
    }

    Integer getTotalStats() {
        return strength + health + agility + charisma + intelligence + endurance + luck;
    }

    private void levelUp() {
        level++;
        unassignedPoints += 5;
    }

    public Integer getUnassignedPoints() {
        return unassignedPoints;
    }

    private Double addStatExp(Stats stat, Double exp) throws InvalidInputException {
        switch (stat) {
            case STRENGTH:
                strengthExp += exp;
                return strengthExp;
            case HEALTH:
                healthExp += exp;
                return healthExp;
            case AGILITY:
                agilityExp += exp;
                return agilityExp;
            case CHARISMA:
                charismaExp += exp;
                return charismaExp;
            case INTELLIGENCE:
                intelligenceExp += exp;
                return intelligenceExp;
            case ENDURANCE:
                enduranceExp += exp;
                return enduranceExp;
            default:
                throw new InvalidInputException("Unknown Player stat for exp: " + stat.name());
        }
    }

    private double getStatExpValue(Stats stat) {
        switch (stat) {
            case STRENGTH:
                return strengthExp;
            case HEALTH:
                return healthExp;
            case AGILITY:
                return agilityExp;
            case CHARISMA:
                return charismaExp;
            case INTELLIGENCE:
                return intelligenceExp;
            case ENDURANCE:
                return enduranceExp;
            default:
                throw new InvalidInputException("Unknown Player stat experience: " + stat.name());
        }
    }

    private Integer getStatValue(Stats stat) {
        switch (stat) {
            case STRENGTH:
                return strength;
            case HEALTH:
                return health;
            case AGILITY:
                return agility;
            case CHARISMA:
                return charisma;
            case INTELLIGENCE:
                return intelligence;
            case ENDURANCE:
                return endurance;
            case LUCK:
                return luck;
            default:
                throw new InvalidInputException("Unknown Player stat: " + stat.name());
        }
    }

    String getInfoRow(Stats stat, Boolean includeExperience, Language lang, Boolean useMarkup) {
        return (useMarkup ? "*" : "") + stat.emoji() + stat.what(lang)
                + ":" + (useMarkup ? "* " : " ") + getStatValue(stat)
                + (!includeExperience || !stat.containsExperience() ? "" : " (" + getStatExpValue(stat) + "/" + getExpToNextStatUp(getStatValue(stat)) + ")");
    }

    public boolean hasStats(Map<Stats, Integer> requiredStats) {
        for (Stats stat : requiredStats.keySet()) {
            if (getStatValue(stat) < requiredStats.get(stat)) return false;
        }
        return true;
    }

}
