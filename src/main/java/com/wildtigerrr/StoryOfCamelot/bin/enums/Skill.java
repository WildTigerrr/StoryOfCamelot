package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.enums.skill.SkillCondition;
import com.wildtigerrr.StoryOfCamelot.bin.enums.skill.SkillTarget;
import com.wildtigerrr.StoryOfCamelot.bin.enums.skill.SkillType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.PlayerStats;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import lombok.Getter;

import java.util.*;

public enum Skill {

    DO_NOTHING(SkillType.DEFENCE, SkillTarget.SELF, null, null, null, NameTranslation.BUTTON_BACK, null),
    BASIC_ATTACK(SkillType.ATTACK, SkillTarget.ENEMY, new HashMap<>() {{
        put(SkillCondition.SKILL_STRENGTH, 1.0F);
    }}, null, null, NameTranslation.SKILL_FIGHT_ATTACK, null),
    STRONG_ATTACK(SkillType.ATTACK, SkillTarget.ENEMY, new HashMap<>() {{
        put(SkillCondition.SKILL_STRENGTH, 1.5F);
    }}, null, null, NameTranslation.SKILL_FIGHT_STRONG_ATTACK, null),
    ADVANCED_STRONG_ATTACK(SkillType.ATTACK, SkillTarget.ENEMY, new HashMap<>() {{
        put(SkillCondition.SKILL_STRENGTH, 2.5F);
    }}, new ArrayList<>() {{
        add(STRONG_ATTACK);
    }}, new HashMap<>() {{
        put(Stats.STRENGTH, 10);
    }}, NameTranslation.SKILL_FIGHT_ADVANCED_STRONG_ATTACK, NameTranslation.DESC_SKILL_ADVANCED_STRONG_ATTACK),
    FAST_ATTACK(SkillType.ATTACK, SkillTarget.ENEMY, new HashMap<>() {{
        put(SkillCondition.SKILL_STRENGTH, 0.5F);
        put(SkillCondition.SKILL_AGILITY, 1.0F);
    }}, null, null, NameTranslation.SKILL_FIGHT_FAST_ATTACK, NameTranslation.DESC_SKILL_FAST_ATTACK),
    BASIC_DEFENCE(SkillType.DEFENCE, SkillTarget.SELF, new HashMap<>() {{
        put(SkillCondition.SKILL_STRENGTH, 1.0F);
    }}, null, null, NameTranslation.SKILL_FIGHT_DEFENCE, null);

    @Getter
    private final SkillType type;
    @Getter
    private final SkillTarget target;
    private final Map<SkillCondition, Float> multipliers;
    private final List<Skill> requiredSkills;
    private final Map<Stats, Integer> requiredStats;
    private final NameTranslation label;
    private final NameTranslation description;

    Skill(SkillType type, SkillTarget target, Map<SkillCondition, Float> multipliers, List<Skill> requiredSkills, Map<Stats, Integer> requiredStats, NameTranslation label, NameTranslation description) {
        this.type = type;
        this.target = target;
        this.multipliers = multipliers;
        this.requiredSkills = requiredSkills;
        this.requiredStats = requiredStats;
        this.label = label;
        this.description = description;
    }

    public String getLabel(Language lang) {
        return label.getName(lang);
    }

    public String getDescription(Language lang) {
        if (description == null) return "";
        return description.getName(lang);
    }

    public NameTranslation getLabelTranslations() {
        return label;
    }

    public float calculateStrength(Player player) {
        if (multipliers == null || multipliers.isEmpty()) return 0;
        float result = 0.0F;
        for (SkillCondition sk : multipliers.keySet()) {
            switch (sk) {
                case SKILL_STRENGTH:
                    result += (player.stats().getStrength() * multipliers.get(sk));
                    break;
                case SKILL_AGILITY:
                    result += player.stats().getAgility() * multipliers.get(sk);
                    break;
                case SKILL_INTELLIGENCE:
                    result += player.stats().getIntelligence() * multipliers.get(sk);
                    break;
            }
        }
        return result;
    }

    public static Set<Skill> getAvailableSkills(Set<Skill> existingSkills, PlayerStats playerStats) {
        Set<Skill> available = new HashSet<>();
        for (Skill skill : Skill.values()) {
            if ((skill.requiredSkills == null || (existingSkills != null && existingSkills.containsAll(skill.requiredSkills))) &&
                    (skill.requiredStats == null || playerStats.hasStats(skill.requiredStats))) {
                available.add(skill);
            }
        }
        return available;
    }

    public static Set<Skill> getAvailableSkills(Player player) {
        return getAvailableSkills(player.getSkills(), player.stats());
    }

}
