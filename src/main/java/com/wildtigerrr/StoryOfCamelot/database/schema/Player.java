package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.exceptions.SOCInvalidDataException;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "player")
public class Player {

    // ==================================================== MAIN ==================================================== //

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String externalId;
    private String nickname;

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

    public void setNickname(String nickname) {
        this.nickname = removeSpecialCharacters(nickname);
    }

    // ------------------- CONSTRUCTORS ----------------------------------------------------------------------------- //

    protected Player() {
    }

    public Player(String externalId, String nickname, Location location) {
        this.externalId = externalId;
        this.nickname = nickname;
        this.location = location;
        isNew = externalId.equals(nickname);
        level = 1;
        unassignedPoints = 25;
        strength = 0;
        health = 0;
        agility = 0;
        intelligence = 0;
        endurance = 0;
        luck = 0;

        strengthExp = 0;
        healthExp = 0;
        agilityExp = 0;
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
    private Integer intelligence;
    private Integer endurance;
    private Integer luck;

    private Integer strengthExp;
    private Integer healthExp;
    private Integer agilityExp;
    private Integer intelligenceExp;
    private Integer enduranceExp;

    // ------------------- LEVEL UP CALCULATION ------------------------------------------------------------------------ //

    private Integer getExpToNextStatUp(Integer currentLevel) {
        return ((int) Math.pow(2, currentLevel - 1));
    }

    private Integer getStatsToNextLevelUp() {
        return 5 * level * (level + 1) / 2 - (unassignedPoints - (20 + (5 * level)));
    }

    // ------------------- LEVEL UP MECHANIC ------------------------------------------------------------------------ //

    public ArrayList<String> addStatExp(Integer exp, Stats stat) throws SOCInvalidDataException {
        ArrayList<String> events = new ArrayList<>();
        Boolean up = isStatUp(stat, exp);
        while (up) {
            switch (stat) {
                case STRENGTH:
                    strengthExp -= getExpToNextStatUp(strength);
                    strength++;
                    events.add(MainText.STAT_UP_START.text() + Stats.STRENGTH.which() + MainText.STAT_UP_END.text() + strength);
                    break;
                case HEALTH:
                    healthExp -= getExpToNextStatUp(health);
                    health++;
                    events.add(MainText.STAT_UP_START.text() + Stats.HEALTH.which() + MainText.STAT_UP_END.text() + strength);
                    break;
                case AGILITY:
                    agilityExp -= getExpToNextStatUp(agility);
                    agility++;
                    events.add(MainText.STAT_UP_START.text() + Stats.AGILITY.which() + MainText.STAT_UP_END.text() + agility);
                    break;
                case INTELLIGENCE:
                    intelligenceExp -= getExpToNextStatUp(intelligence);
                    intelligence++;
                    events.add(MainText.STAT_UP_START.text() + Stats.INTELLIGENCE.which() + MainText.STAT_UP_END.text() + intelligence);
                    break;
                case ENDURANCE:
                    enduranceExp -= getExpToNextStatUp(endurance);
                    endurance++;
                    events.add(MainText.STAT_UP_START.text() + Stats.ENDURANCE.which() + MainText.STAT_UP_END.text() + endurance);
                    break;
            }
            if (isLevelUp()) {
                levelUp();
                events.add(MainText.LEVEL_UP.text() + getLevel());
            }
            up = isStatUp(stat, 0);
        }
        return events;
    }

    private Boolean isStatUp(Stats stat, Integer newExp) throws SOCInvalidDataException {
        return getCurrentStatExp(stat, newExp) >= getExpToNextStatUp(getCurrentStat(stat));
    }

    private Boolean isLevelUp() {
        return getTotalStats() >= getStatsToNextLevelUp();
    }

    private Integer getTotalStats() {
        return strength + health + agility + intelligence + endurance + luck;
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

    private String status;
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
    public String toString() {
        return "Если память тебя не подводит, то:"
                + "\n*" + this.nickname + "*, " + this.level + " уровень (" + getTotalStats() + "/" + getStatsToNextLevelUp() +")"
                + (getUnassignedPoints() > 0 ? " (+" + getUnassignedPoints() + ")" : "")
                + "\n*Сила:* " + this.strength + " (" + this.strengthExp + "/" + getExpToNextStatUp(this.strength) + ")"
                + "\n*Здоровье:* " + this.health + " (" + this.healthExp + "/" + getExpToNextStatUp(this.health) + ")"
                + "\n*Ловкость:* " + this.agility + " (" + this.agilityExp + "/" + getExpToNextStatUp(this.agility) + ")"
                + "\n*Интеллект:* " + this.intelligence + " (" + this.intelligenceExp + "/" + getExpToNextStatUp(this.intelligence) + ")"
                + "\n*Выносливость:* " + this.endurance + " (" + this.enduranceExp + "/" + getExpToNextStatUp(this.endurance) + ")"
                + "\n*Удача:* " + this.luck
                + "\n\n_Что же ещё известно?_";
    }

    private String removeSpecialCharacters(String newNickname) {
        newNickname = newNickname.replaceAll("[^a-zA-Z\\s]", "");
        if (newNickname.length() > 25) newNickname = newNickname.substring(0, 35);
        return newNickname;
    }

    // ================================================ END SERVICE ================================================= //
}
