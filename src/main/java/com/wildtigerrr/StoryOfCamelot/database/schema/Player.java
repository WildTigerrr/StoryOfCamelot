package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.exceptions.SOCInvalidDataException;
import com.wildtigerrr.StoryOfCamelot.bin.service.SpringManager;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.Comparator.*;

@Entity
@Table(name = "player")
public class Player implements Comparable<Player>, Fighter {

    // ==================================================== MAIN ==================================================== //

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String externalId;
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Language language;
    @Enumerated(EnumType.STRING)
    private PlayerStatus status;
    @Enumerated(EnumType.STRING)
    private PlayerStatusExtended additionalStatus;

    // ------------------- GETTERS AND SETTERS ---------------------------------------------------------------------- //

    public Integer getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    // TODO Admin method for setting another external Id
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getNickname() {
        return nickname;
    }

    public Boolean setNickname(String nickname) {
        if (nickname.length() > getNicknameLengthMax()) {
            return false;
        } else {
            this.nickname = nickname;
            return true;
        }
    }

    public PlayerStatus getStatus() {
        return status;
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

    public PlayerStatusExtended getAdditionalStatus() {
        return additionalStatus;
    }

    public void setAdditionalStatus(PlayerStatusExtended additionalStatus) {
        this.additionalStatus = additionalStatus;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    // ------------------- CONSTRUCTORS ----------------------------------------------------------------------------- //

    protected Player() {
    }

    public Player(String externalId, String nickname, Location location) {
        this.externalId = Objects.requireNonNull(externalId);
        this.nickname = nickname;
        this.location = location;
        isNew = externalId.equals(nickname);
        level = 1;
        language = Language.RUS;
        status = PlayerStatus.TUTORIAL;
        additionalStatus = PlayerStatusExtended.LANGUAGE_CHOOSE;
        unassignedPoints = getDefaultPoints() + 5;
        strength = 1;
        health = 1;
        agility = 1;
        charisma = 1;
        intelligence = 1;
        endurance = 1;
        luck = 1;

        strengthExp = 0;
        healthExp = 0;
        agilityExp = 0;
        charismaExp = 0;
        intelligenceExp = 0;
        enduranceExp = 0;
    }

    // ================================================== END MAIN ================================================== //

    // ================================================ LEVEL SYSTEM ================================================ //

    private Integer level;

    private Integer unassignedPoints;

    private Integer strength;
    private Integer health;
    private Integer agility;
    private Integer charisma;
    private Integer intelligence;
    private Integer endurance;
    private Integer luck;

    private Integer strengthExp;
    private Integer healthExp;
    private Integer agilityExp;
    private Integer charismaExp;
    private Integer intelligenceExp;
    private Integer enduranceExp;

    // ------------------- LEVEL UP CALCULATION ------------------------------------------------------------------------ //

    private int getExpToNextStatUp(Integer currentLevel) {
        return (int) Math.pow(currentLevel + 1, 2);
    }

    private int getStatsToNextLevelUp() {
        return 5 * level * (level + 1) / 2;
    }

    private int getDefaultPoints() {
        return 20;
    }

    private int getAssignedPoints() {
        return (7 - unassignedPoints + (getDefaultPoints() + (5 * level)));
    }

    // ------------------- LEVEL UP MECHANIC ------------------------------------------------------------------------ //

    public String raiseStat(Stats stat, Integer quantity, Language lang, TranslationManager translation) {
        if (quantity > unassignedPoints) return translation.get(lang).statInsufficientPoints(); // MainText.STAT_INSUFFICIENT_POINTS.text(lang);
        int newQuantity;
        switch (stat) {
            case STRENGTH:
                strengthExp = 0;
                strength += quantity;
                newQuantity = strength;
                break;
            case HEALTH:
                healthExp = 0;
                health += quantity;
                newQuantity = health;
                break;
            case AGILITY:
                agilityExp = 0;
                agility += quantity;
                newQuantity = agility;
                break;
            case CHARISMA:
                charismaExp = 0;
                charisma += quantity;
                newQuantity = charisma;
                break;
            case INTELLIGENCE:
                intelligenceExp = 0;
                intelligence += quantity;
                newQuantity = intelligence;
                break;
            case ENDURANCE:
                enduranceExp = 0;
                endurance += quantity;
                newQuantity = endurance;
                break;
            case LUCK:
                luck += quantity;
                newQuantity = luck;
                break;
            default:
                return translation.get(lang).statInvalid(); // MainText.STAT_INVALID.text(lang);
        }
        unassignedPoints -= quantity;
//        return MainText.STAT_UP.text(lang, stat.whichLowercase(lang), String.valueOf(newQuantity));
        return translation.get(lang).statUp(stat.whichLowercase(lang), String.valueOf(newQuantity));
    }

    public ArrayList<String> addStatExpByName(Integer exp, Stats stat, Language lang, TranslationManager translation) throws SOCInvalidDataException, NoSuchFieldException {
        ArrayList<String> events = new ArrayList<>();
        if (stat == Stats.LUCK) {
            events.add(translation.get(lang).statCannotBeRaised(stat.which(lang))); // MainText.STAT_CANNOT_BE_RAISED.text(lang, stat.which(lang))
        }
        Boolean up = isStatUp(stat, exp);
        if (up) {
            Field expField = Player.class.getDeclaredField("strengthExp");
        }
        while (up) {

            if (isLevelUp()) {
                levelUp();
                events.add(translation.get(lang).levelUp(String.valueOf(getLevel()))); // MainText.LEVEL_UP.text(lang, String.valueOf(getLevel()))
            }
            up = isStatUp(stat, 0);
        }
        return events;
    }

    public ArrayList<String> addStatExp(Integer exp, Stats stat, Language lang, TranslationManager translation) throws SOCInvalidDataException {
        ArrayList<String> events = new ArrayList<>();
        Boolean up = isStatUp(stat, exp);
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
                    throw new SOCInvalidDataException("Invalid stat: " + stat);
            }
            events.add(statUp(lang, stat.whichLowercase(lang), currentValue, translation)); // MainText.STAT_UP.text(lang, stat.whichLowercase(lang), currentValue)
            if (isLevelUp()) {
                levelUp();
                events.add(translation.get(lang).levelUp(String.valueOf(getLevel()))); // MainText.LEVEL_UP.text(lang, String.valueOf(getLevel()))
            }
            up = isStatUp(stat, 0);
        }
        return events;
    }

    private String statUp(Language lang, String statName, String value, TranslationManager translation) {
        return translation.get(lang).statUp(statName, value);
    }

    private Boolean isStatUp(Stats stat, Integer newExp) throws SOCInvalidDataException {
        return getCurrentStatExp(stat, newExp) >= getExpToNextStatUp(getCurrentStat(stat));
    }

    private Boolean isLevelUp() {
        return getTotalStats() - getAssignedPoints() >= getStatsToNextLevelUp();
    }

    private Integer getTotalStats() {
        return strength + health + agility + charisma + intelligence + endurance + luck;
    }

    // ------------------- GETTERS AND SETTERS ---------------------------------------------------------------------- //

    public Integer getLevel() {
        return level;
    }

    private void levelUp() {
        level++;
        unassignedPoints += 5;
    }

    public Integer getUnassignedPoints() {
        return unassignedPoints;
    }

    private Integer getCurrentStatExp(Stats stat, Integer exp) throws SOCInvalidDataException {
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
                throw new SOCInvalidDataException("Unknown Player stat for exp: " + stat.name());
        }
    }

    private Integer getCurrentStat(Stats stat) throws SOCInvalidDataException {
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
            default:
                throw new SOCInvalidDataException("Unknown Player stat: " + stat.name());
        }
    }

    public Integer getStrength() {
        return strength;
    }

    public Integer getHealth() {
        return health;
    }

    public Integer getAgility() {
        return agility;
    }

    public Integer getCharisma() {
        return charisma;
    }

    public Integer getIntelligence() {
        return intelligence;
    }

    public Integer getEndurance() {
        return endurance;
    }

    public Integer getLuck() {
        return luck;
    }

    // ============================================== END LEVEL SYSTEM ============================================== //

    // ================================================= FINAL STATS ================================================ //

    private Integer hitpoints;
    private Integer hitpointsMax;
    private Integer damage;
    private Integer speed;
    private Integer hunger;

    @Override
    public Integer getDamage() {
        return getStrength();
    }

    @Override
    public Integer getDefence() {
        return getEndurance();
    }

    @Override
    public String getName(Language lang) {
        return nickname;
    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public void applyDamage(int damage) {
        health -= damage;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

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
        return isNew;
    }

    public void setup() {
        this.isNew = false;
        this.level = 1;
    }

    public List<Backpack> getBackpacks() {
        return backpacks;
    }

    public void setBackpacks(List<Backpack> backpacks) {
        this.backpacks = backpacks;
    }

    public void addBackpack(Backpack backpack) {
        backpacks.add(backpack);
        backpack.setPlayer(this);
    }

    public void removeBackpack(Backpack backpack) {
        this.backpacks.remove(backpack);
    }

    public FileLink getImageLink() {
        return imageLink;
    }

    public void setImageLink(FileLink imageLink) {
        this.imageLink = imageLink;
    }


    // ================================================== SERVICE =================================================== //

    @Override
    public String toString() { // MainText.IF_I_REMEMBER.text(language) MainText.LEVEL.text(language) MainText.WHAT_ELSE_WE_KNOW.text(language)
        TranslationManager translation = SpringManager.bean(TranslationManager.class);
        return  translation.get(language).ifIRemember() + ":"
                + "\n*" + this.nickname + "*, " + this.level + " " + translation.get(language).level().toLowerCase() + " (" + (getTotalStats() - getAssignedPoints()) + "/" + getStatsToNextLevelUp() + ")"
                + (getUnassignedPoints() > 0 ? " (+" + getUnassignedPoints() + ")" : "")
                + "\n*" + Stats.STRENGTH.what(language) + ":* " + this.strength + " (" + this.strengthExp + "/" + getExpToNextStatUp(this.strength) + ")"
                + "\n*" + Stats.HEALTH.what(language) + ":* " + this.health + " (" + this.healthExp + "/" + getExpToNextStatUp(this.health) + ")"
                + "\n*" + Stats.AGILITY.what(language) + ":* " + this.agility + " (" + this.agilityExp + "/" + getExpToNextStatUp(this.agility) + ")"
                + "\n*" + Stats.CHARISMA.what(language) + ":* " + this.charisma + " (" + this.charismaExp + "/" + getExpToNextStatUp(this.charisma) + ")"
                + "\n*" + Stats.INTELLIGENCE.what(language) + ":* " + this.intelligence + " (" + this.intelligenceExp + "/" + getExpToNextStatUp(this.intelligence) + ")"
                + "\n*" + Stats.ENDURANCE.what(language) + ":* " + this.endurance + " (" + this.enduranceExp + "/" + getExpToNextStatUp(this.endurance) + ")"
                + "\n*" + Stats.LUCK.what(language) + ":* " + this.luck
                + "\n\n_" + translation.get(language).whatElseWeKnow() + "?_";
    }

    public String toStatString(int index) {
        return index + ". " + this.nickname + ", " + getLevel()  + " (" + getTotalStats() + ")" + "\n";
    }

    public String getStatMenu(TranslationManager translation) {
        int unassigned = getUnassignedPoints(); // MainText.LEVEL.text(language)
        return nickname + ", " + level + " " + translation.get(language).level().toLowerCase() + " (+" + unassigned + ")"
                + "\n\n" + Stats.STRENGTH.emoji() + Stats.STRENGTH.what(language) + ": " + strength
                + "\n" + Stats.HEALTH.emoji() + Stats.HEALTH.what(language) + ": " + health
                + "\n" + Stats.AGILITY.emoji() + Stats.AGILITY.what(language) + ": " + agility
                + "\n" + Stats.CHARISMA.emoji() + Stats.CHARISMA.what(language) + ": " + charisma
                + "\n" + Stats.INTELLIGENCE.emoji() + Stats.INTELLIGENCE.what(language) + ": " + intelligence
                + "\n" + Stats.ENDURANCE.emoji() + Stats.ENDURANCE.what(language) + ": " + endurance
                + "\n" + Stats.LUCK.emoji() + Stats.LUCK.what(language) + ": " + luck;
    }

    public static int getNicknameLengthMax() {
        return 40;
    }

    /*private String removeSpecialCharacters(String newNickname) {
        newNickname = newNickname.replaceAll("[^a-zA-Z\\s]", "");
        if (newNickname.length() > 25) newNickname = newNickname.substring(0, 35);
        return newNickname.trim();
    }*/

    public static Boolean containsSpecialCharacters(String newNickname) {
        String updated = newNickname.replaceAll(" {2,}", " ").replaceAll("[*_`\\\\/]", "");
        return !newNickname.equals(updated);
    }

    @Override
    public int compareTo(Player p) {
        return getComparator().compare(this, p);
    }

    private static Comparator<Player> getComparator(){
        return comparing(Player::getLevel, reverseOrder())
                .thenComparing(Player::getTotalStats, reverseOrder());
    }

    // ================================================ END SERVICE ================================================= //
}
