package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.bin.GameEvent;
import com.wildtigerrr.StoryOfCamelot.bin.MainText;
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

    // TODO admin method for setting
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

    public Player(String externalId, String nickname) {
        this.externalId = externalId;
        this.nickname = nickname;
        this.isNew = externalId.equals(nickname);
        strength = 5;
        agility = 5;
        intelligence = 5;
        endurance = 5;
        luck = 5;

        strengthExp = 0;
        agilityExp = 0;
        intelligenceExp = 0;
        enduranceExp = 0;
    }

    // ================================================== END MAIN ================================================== //


    // ================================================ LEVEL SYSTEM ================================================ //

    private Integer level;

    private Integer unassignedPoints;

    private Integer strength;
    private Integer agility;
    private Integer intelligence;
    private Integer endurance;
    private Integer luck;

    private Integer strengthExp;
    private Integer agilityExp;
    private Integer intelligenceExp;
    private Integer enduranceExp;

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

    public ArrayList<String> addStatExp(Integer exp, Stats stat) throws SOCInvalidDataException {
        ArrayList<String> events = new ArrayList<>();
        if (isStatUp(stat, exp)) {
            switch (stat) {
                case STRENGTH:
                    while (strengthExp > getExpToNextStatUp(strength)) {
                        strengthExp -= getExpToNextStatUp(strength);
                        strength++;
                        events.add(MainText.STAT_UP_START.text() + Stats.STRENGTH.which() + MainText.STAT_UP_END.text());
                    }
                    break;
                case AGILITY:
                    while (agilityExp > getExpToNextStatUp(agility)) {
                        agilityExp -= getExpToNextStatUp(agility);
                        agility++;
                        events.add(MainText.STAT_UP_START.text() + Stats.AGILITY.which() + MainText.STAT_UP_END.text());
                    }
                    break;
                case INTELLIGENCE:
                    while (intelligenceExp > getExpToNextStatUp(intelligence)) {
                        intelligenceExp -= getExpToNextStatUp(intelligence);
                        intelligence++;
                        events.add(MainText.STAT_UP_START.text() + Stats.INTELLIGENCE.which() + MainText.STAT_UP_END.text());
                    }
                    break;
                case ENDURANCE:
                    while (intelligenceExp > getExpToNextStatUp(endurance)) {
                        enduranceExp -= getExpToNextStatUp(endurance);
                        endurance++;
                        events.add(MainText.STAT_UP_START.text() + Stats.ENDURANCE.which() + MainText.STAT_UP_END.text());
                    }
                    break;
            }
            events.add(isLevelUp());
            return events;
        }
        return null;
    }

    private Boolean isStatUp(Stats stat, Integer newExp) throws SOCInvalidDataException {
        return getCurrentStatExp(stat, newExp) >= getExpToNextStatUp(getCurrentStat(stat));
    }

    private Integer getExpToNextStatUp(Integer currentLevel) {
        return ((int) (10 * Math.pow(2, currentLevel - 1)));
    }

    private String isLevelUp() {
        if ((strength + agility + intelligence + endurance + luck) >= (5 * level + (20 + level * 5))) {
            System.out.println("Level up! Player: " + nickname);
            levelUp();
            return MainText.LEVEL_UP.text() + getLevel();
        }
        return null;
    }

    public Integer getStrength() {
        return strength;
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

    private Integer hitpoints;
    private Integer hitpointsMax;
    private Integer damage;
    private Integer speed;

    private Integer silver;
    private Integer gold;
    private Integer diamonds;

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

    public Integer getHitpoints() {
        return hitpoints;
    }

    public void setHitpoints(Integer hitpoints) {
        this.hitpoints = hitpoints;
    }

    public Integer getHitpointsMax() {
        return hitpointsMax;
    }

    public void setHitpointsMax(Integer hitpointsMax) {
        this.hitpointsMax = hitpointsMax;
    }

    public Integer getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
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

    public void setImage_link(FileLink imageLink) {
        this.imageLink = imageLink;
    }


    // ================================================== SERVICE =================================================== //

    @Override
    public String toString() {
        return "Если память тебя не подводит, то:"
                + "\n*" + this.nickname + "*, " + this.level + " уровень"
                + "\n*Сила:* " + this.strength + " (" + this.strengthExp + "/" + getExpToNextStatUp(this.strength) + ")"
                + "\n*Ловкость:* " + this.agility + " (" + this.agilityExp + "/" + getExpToNextStatUp(this.agility) + ")"
                + "\n*Интеллект:* " + this.intelligence + " (" + this.intelligenceExp + "/" + getExpToNextStatUp(this.intelligence) + ")"
                + "\n*Выносливость:* " + this.endurance + " (" + this.enduranceExp + "/" + getExpToNextStatUp(this.endurance) + ")"
                + "\n*Удача:* " + this.luck
                + "\n\n_Что же ещё известно?_";
    }

    private String removeSpecialCharacters(String nickname) {
        nickname = nickname.replaceAll("[^a-zA-Z\\s]", "");
        if (nickname.length() > 25) nickname = nickname.substring(0, 35);
        return nickname;
    }

    // ================================================ END SERVICE ================================================= //
}
