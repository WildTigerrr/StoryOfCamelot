package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.enums.skill.SkillCondition;
import com.wildtigerrr.StoryOfCamelot.bin.enums.skill.SkillTarget;
import com.wildtigerrr.StoryOfCamelot.bin.enums.skill.SkillType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum Skill {

    DO_NOTHING(SkillType.DEFENCE, SkillTarget.SELF, null),
    BASIC_ATTACK(SkillType.ATTACK, SkillTarget.ENEMY, new HashMap<>() {{
        put(SkillCondition.SKILL_STRENGTH, 1.0);
    }}),
    STRONG_ATTACK(SkillType.ATTACK, SkillTarget.ENEMY, new HashMap<>() {{
        put(SkillCondition.SKILL_STRENGTH, 1.5);
    }}),
    FAST_ATTACK(SkillType.ATTACK, SkillTarget.ENEMY, new HashMap<>() {{
        put(SkillCondition.SKILL_STRENGTH, 0.5);
        put(SkillCondition.SKILL_AGILITY, 1.0);
    }}),
    BASIC_DEFENCE(SkillType.DEFENCE, SkillTarget.SELF, new HashMap<>() {{
        put(SkillCondition.SKILL_STRENGTH, 1.0);
    }});

    @Getter
    private final SkillType type;
    @Getter
    private final SkillTarget target;
    private final Map<SkillCondition, Double> multipliers;

    Skill(SkillType type, SkillTarget target, Map<SkillCondition, Double> multipliers) {
        this.type = type;
        this.target = target;
        this.multipliers = multipliers;
    }

    public double calculateStrength(Player player) {
        if (multipliers == null || multipliers.isEmpty()) return 0;
        double result = 0.0;
        for (SkillCondition sk : multipliers.keySet()) {
            switch (sk) {
                case SKILL_STRENGTH: result += (player.stats().getStrength() * multipliers.get(sk)); break;
                case SKILL_AGILITY: result += player.stats().getAgility() * multipliers.get(sk); break;
                case SKILL_INTELLIGENCE: result += player.stats().getIntelligence() * multipliers.get(sk); break;
            }
        }
        return result;
    }

}
