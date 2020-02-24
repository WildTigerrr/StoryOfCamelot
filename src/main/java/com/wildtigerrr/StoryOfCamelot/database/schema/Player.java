package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import com.wildtigerrr.StoryOfCamelot.bin.service.SpringManager;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.web.BotConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.Comparator.*;

@Entity
@Table(name = "player")
@Getter
@Setter
public class Player implements Comparable<Player>, Fighter {

    // ==================================================== MAIN ==================================================== //

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Integer id;
    private String externalId; // TODO Admin method for setting another external Id
    @Setter(AccessLevel.NONE)
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Language language;
    @Enumerated(EnumType.STRING)
    private PlayerStatus status;
    @Enumerated(EnumType.STRING)
    private PlayerStatusExtended additionalStatus;

    @Embedded
    private PlayerStats stats;

    // ------------------- GETTERS AND SETTERS ---------------------------------------------------------------------- //

    public Boolean setNickname(String nickname) {
        if (nickname.length() > getNicknameLengthMax()) {
            return false;
        } else {
            this.nickname = nickname;
            return true;
        }
    }

    public void ban() {
        status = PlayerStatus.BANNED;
    }

    public void activate() {
        status = PlayerStatus.ACTIVE;
    }

    public void move() {
        status = PlayerStatus.MOVEMENT;
    }

    public void stop() {
        if (additionalStatus.getNumber() == 0) {
            status = PlayerStatus.ACTIVE;
        } else if (additionalStatus.getNumber() > 100) {
            status = PlayerStatus.BANNED;
        } else {
            status = PlayerStatus.TUTORIAL;
        }
    }

    // ------------------- CONSTRUCTORS ----------------------------------------------------------------------------- //

    protected Player() {
    }

    public Player(String externalId, String nickname, Location location) {
        this.externalId = Objects.requireNonNull(externalId);
        this.nickname = nickname;
        this.location = location;
        isNew = externalId.equals(nickname);
//        level = 1;
        language = Language.RUS;
        status = PlayerStatus.TUTORIAL;
        additionalStatus = PlayerStatusExtended.LANGUAGE_CHOOSE;
        stats.setUnassignedPoints(stats.getDefaultPoints() + 5);
//        strength = 1;
//        health = 1;
//        agility = 1;
//        charisma = 1;
//        intelligence = 1;
//        endurance = 1;
//        luck = 1;
//
//        strengthExp = 0;
//        healthExp = 0;
//        agilityExp = 0;
//        charismaExp = 0;
//        intelligenceExp = 0;
//        enduranceExp = 0;
    }

    // ================================================== END MAIN ================================================== //

    // ================================================ LEVEL SYSTEM ================================================ //

//    @Setter(AccessLevel.NONE)
//    private Integer level;
//
//    private Integer unassignedPoints;
//
//    private Integer strength;
//    private Integer health;
//    private Integer agility;
//    private Integer charisma;
//    private Integer intelligence;
//    private Integer endurance;
//    private Integer luck;
//
//    private Integer strengthExp;
//    private Integer healthExp;
//    private Integer agilityExp;
//    private Integer charismaExp;
//    private Integer intelligenceExp;
//    private Integer enduranceExp;

    // ------------------- LEVEL UP CALCULATION ------------------------------------------------------------------------ //

//    private int getExpToNextStatUp(Integer currentLevel) {
//        return (int) Math.pow(currentLevel + 1, 2);
//    }
//
//    private int getStatsToNextLevelUp() {
//        return 5 * level * (level + 1) / 2;
//    }
//
//    private int getDefaultPoints() {
//        return BotConfig.DEFAULT_SKILL_POINTS;
//    }
//
//    private int getAssignedPoints() {
//        return (7 - unassignedPoints + (getDefaultPoints() + (5 * level)));
//    }

    // ------------------- LEVEL UP MECHANIC ------------------------------------------------------------------------ //

//    public String raiseStat(Stats stat, Integer quantity, Language lang, TranslationManager translation) {
//        if (quantity > unassignedPoints) return translation.getMessage("player.stats.insufficient-points", lang);
//        int newQuantity;
//        switch (stat) {
//            case STRENGTH:
//                strengthExp = 0;
//                strength += quantity;
//                newQuantity = strength;
//                break;
//            case HEALTH:
//                healthExp = 0;
//                health += quantity;
//                newQuantity = health;
//                break;
//            case AGILITY:
//                agilityExp = 0;
//                agility += quantity;
//                newQuantity = agility;
//                break;
//            case CHARISMA:
//                charismaExp = 0;
//                charisma += quantity;
//                newQuantity = charisma;
//                break;
//            case INTELLIGENCE:
//                intelligenceExp = 0;
//                intelligence += quantity;
//                newQuantity = intelligence;
//                break;
//            case ENDURANCE:
//                enduranceExp = 0;
//                endurance += quantity;
//                newQuantity = endurance;
//                break;
//            case LUCK:
//                luck += quantity;
//                newQuantity = luck;
//                break;
//            default:
//                return translation.getMessage("player.stats.invalid", lang);
//        }
//        unassignedPoints -= quantity;
//        return translation.getMessage("player.stats.stat-up", lang, new Object[]{stat.whichLowercase(lang), String.valueOf(newQuantity)});
//    }
//
//    public ArrayList<String> addStatExp(Integer exp, Stats stat, Language lang, TranslationManager translation) {
//        ArrayList<String> events = new ArrayList<>();
//        Boolean up = isStatUp(stat, exp);
//        String currentValue;
//        while (up) {
//            switch (stat) {
//                case STRENGTH:
//                    strengthExp -= getExpToNextStatUp(strength);
//                    strength++;
//                    currentValue = String.valueOf(strength);
//                    break;
//                case HEALTH:
//                    healthExp -= getExpToNextStatUp(health);
//                    health++;
//                    currentValue = String.valueOf(health);
//                    break;
//                case AGILITY:
//                    agilityExp -= getExpToNextStatUp(agility);
//                    agility++;
//                    currentValue = String.valueOf(agility);
//                    break;
//                case CHARISMA:
//                    charismaExp -= getExpToNextStatUp(charisma);
//                    charisma++;
//                    currentValue = String.valueOf(charisma);
//                    break;
//                case INTELLIGENCE:
//                    intelligenceExp -= getExpToNextStatUp(intelligence);
//                    intelligence++;
//                    currentValue = String.valueOf(intelligence);
//                    break;
//                case ENDURANCE:
//                    enduranceExp -= getExpToNextStatUp(endurance);
//                    endurance++;
//                    currentValue = String.valueOf(endurance);
//                    break;
//                default:
//                    throw new InvalidInputException("Invalid stat: " + stat);
//            }
//            events.add(statUp(lang, stat.whichLowercase(lang), currentValue, translation)); // MainText.STAT_UP.text(lang, stat.whichLowercase(lang), currentValue)
//            if (isLevelUp()) {
//                levelUp();
//                events.add(translation.getMessage("player.stats.level-up", lang, new Object[]{String.valueOf(getLevel())}));
//            }
//            up = isStatUp(stat, 0);
//        }
//        return events;
//    }
//
//    private String statUp(Language lang, String statName, String value, TranslationManager translation) {
//        return translation.getMessage("player.stats.stat-up", lang, new Object[]{statName, value});
//    }
//
//    private Boolean isStatUp(Stats stat, Integer newExp) {
//        return getCurrentStatExp(stat, newExp) >= getExpToNextStatUp(getCurrentStat(stat));
//    }
//
//    private Boolean isLevelUp() {
//        return getTotalStats() - getAssignedPoints() >= getStatsToNextLevelUp();
//    }
//
//    private Integer getTotalStats() {
//        return strength + health + agility + charisma + intelligence + endurance + luck;
//    }

    // ------------------- GETTERS AND SETTERS ---------------------------------------------------------------------- //

//    private void levelUp() {
//        level++;
//        unassignedPoints += 5;
//    }
//
//    public Integer getUnassignedPoints() {
//        return unassignedPoints;
//    }
//
//    private Integer getCurrentStatExp(Stats stat, Integer exp) throws InvalidInputException {
//        switch (stat) {
//            case STRENGTH:
//                strengthExp += exp;
//                return strengthExp;
//            case HEALTH:
//                healthExp += exp;
//                return healthExp;
//            case AGILITY:
//                agilityExp += exp;
//                return agilityExp;
//            case CHARISMA:
//                charismaExp += exp;
//                return charismaExp;
//            case INTELLIGENCE:
//                intelligenceExp += exp;
//                return intelligenceExp;
//            case ENDURANCE:
//                enduranceExp += exp;
//                return enduranceExp;
//            default:
//                throw new InvalidInputException("Unknown Player stat for exp: " + stat.name());
//        }
//    }
//
//    private Integer getCurrentStat(Stats stat) {
//        switch (stat) {
//            case STRENGTH:
//                return strength;
//            case HEALTH:
//                return health;
//            case AGILITY:
//                return agility;
//            case CHARISMA:
//                return charisma;
//            case INTELLIGENCE:
//                return intelligence;
//            case ENDURANCE:
//                return endurance;
//            default:
//                throw new InvalidInputException("Unknown Player stat: " + stat.name());
//        }
//    }

    // ============================================== END LEVEL SYSTEM ============================================== //

    // ================================================= FINAL STATS ================================================ //

    private Integer hitpoints;
    private Integer hitpointsMax;
    private Integer damage;
    private Integer speed;
    private Integer hunger;

    @Override
    public int getDamage() {
        return stats.getStrength();
    }

    @Override
    public int getDefence() {
        return stats.getEndurance();
    }

    @Override
    public String getName(Language lang) {
        return nickname;
    }

    @Override
    public boolean isAlive() {
        return stats.getHealth() > 0;
    }

    @Override
    public void applyDamage(int damage) {
        stats.setHealth(stats.getHealth() - damage);
    }

    @Override
    public int getHealth() {
        return stats.getHealth();
    }

    public int getLevel() {
        return stats.getLevel();
    }

    public int getTotalStats() {
        return stats.getTotalStats();
    }

    // TODO Battle stats methods

    // ============================================== END BATTLE STATS ============================================== //

    // =================================================== FINANCE ================================================== //

    private Integer silver;
    private Integer gold;
    private Integer diamonds;

    // TODO Finance methods

    // ================================================= END FINANCE ================================================ //

    // ================================================== MOVEMENT ================================================== //

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    public void moveTo(Location targetLocation) {
        setLocation(targetLocation);
    }

    // ================================================ END MOVEMENT ================================================ //

    private Boolean isNew;

    @OneToMany(
            cascade = {CascadeType.ALL},
            mappedBy = "player"
    )
    private List<Backpack> backpacks = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "filelink_id")
    private FileLink imageLink;

    public Boolean isNew() {
        return getIsNew();
    }

    public void setup() {
        this.isNew = false;
    }

    public void addBackpack(Backpack backpack) {
        backpacks.add(backpack);
        backpack.setPlayer(this);
    }

    public void removeBackpack(Backpack backpack) {
        this.backpacks.remove(backpack);
    }

    // ================================================== SERVICE =================================================== //

    @Override
    public String toString() { // MainText.IF_I_REMEMBER.text(language) MainText.LEVEL.text(language) MainText.WHAT_ELSE_WE_KNOW.text(language)
        TranslationManager translation = SpringManager.bean(TranslationManager.class);
        return translation.getMessage("player.info.if-i-remember", language) + ":"
                + "\n*" + this.nickname + "*, " + this.stats.getLevel() + " " + translation.getMessage("player.info.level", language).toLowerCase()
                + " (" + (stats.getTotalStats() - stats.getAssignedPoints()) + "/" + stats.getStatsToNextLevelUp() + ")"
                + (stats.getUnassignedPoints() > 0 ? " (+" + stats.getUnassignedPoints() + ")" : "")
                + "\n*" + Stats.STRENGTH.what(language) + ":* " + this.stats.getStrength() + " (" + this.stats.getStrengthExp() + "/" + stats.getExpToNextStatUp(this.stats.getStrength()) + ")"
                + "\n*" + Stats.HEALTH.what(language) + ":* " + this.stats.getHealth() + " (" + this.stats.getHealthExp() + "/" + stats.getExpToNextStatUp(this.stats.getHealth()) + ")"
                + "\n*" + Stats.AGILITY.what(language) + ":* " + this.stats.getAgility() + " (" + this.stats.getAgilityExp() + "/" + stats.getExpToNextStatUp(this.stats.getAgility()) + ")"
                + "\n*" + Stats.CHARISMA.what(language) + ":* " + this.stats.getCharisma() + " (" + this.stats.getCharismaExp() + "/" + stats.getExpToNextStatUp(this.stats.getCharisma()) + ")"
                + "\n*" + Stats.INTELLIGENCE.what(language) + ":* " + this.stats.getIntelligence() + " (" + this.stats.getIntelligenceExp() + "/" + stats.getExpToNextStatUp(this.stats.getIntelligence()) + ")"
                + "\n*" + Stats.ENDURANCE.what(language) + ":* " + this.stats.getEndurance() + " (" + this.stats.getEnduranceExp() + "/" + stats.getExpToNextStatUp(this.stats.getEndurance()) + ")"
                + "\n*" + Stats.LUCK.what(language) + ":* " + this.stats.getLuck()
                + "\n\n_" + translation.getMessage("player.info.what-else", language) + "?_";
    }

    public String toStatString(int index) {
        return index + ". " + this.nickname + ", " + stats.getLevel() + " (" + stats.getTotalStats() + ")" + "\n";
    }

    public String getStatMenu(TranslationManager translation) {
        int unassigned = stats.getUnassignedPoints();
        return nickname + ", " + stats.getLevel() + " " + translation.getMessage("player.info.level").toLowerCase() + " (+" + unassigned + ")"
                + "\n\n" + Stats.STRENGTH.emoji() + Stats.STRENGTH.what(language) + ": " + stats.getStrength()
                + "\n" + Stats.HEALTH.emoji() + Stats.HEALTH.what(language) + ": " + stats.getHealth()
                + "\n" + Stats.AGILITY.emoji() + Stats.AGILITY.what(language) + ": " + stats.getAgility()
                + "\n" + Stats.CHARISMA.emoji() + Stats.CHARISMA.what(language) + ": " + stats.getCharisma()
                + "\n" + Stats.INTELLIGENCE.emoji() + Stats.INTELLIGENCE.what(language) + ": " + stats.getIntelligence()
                + "\n" + Stats.ENDURANCE.emoji() + Stats.ENDURANCE.what(language) + ": " + stats.getEndurance()
                + "\n" + Stats.LUCK.emoji() + Stats.LUCK.what(language) + ": " + stats.getLuck();
    }

    public static int getNicknameLengthMax() {
        return 40;
    }

    public static Boolean containsSpecialCharacters(String newNickname) {
        String updated = newNickname.replaceAll(" {2,}", " ").replaceAll("[*_`\\\\/]", "");
        return !newNickname.equals(updated);
    }

    @Override
    public int compareTo(Player p) {
        return getComparator().compare(this, p);
    }

    private static Comparator<Player> getComparator() {
        return comparing(Player::getLevel, reverseOrder())
                .thenComparing(Player::getTotalStats, reverseOrder());
    }

    // ================================================ END SERVICE ================================================= //
}
