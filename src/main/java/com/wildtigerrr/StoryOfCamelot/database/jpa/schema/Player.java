package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.service.ApplicationContextProvider;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.Comparator.comparingInt;

@Entity
@Table(name = "player")
@Getter
@Setter
public class Player extends SimpleObject implements Comparable<Player>, Fighter {

    // ==================================================== MAIN ==================================================== //

    @Id
    @SequenceGenerator(name = "player_seq", sequenceName = "player_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_seq")
    @GenericGenerator(
            name = "player_seq",
            strategy = "com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator",
            parameters = {
                    @Parameter(name = IdGenerator.VALUE_PREFIX_PARAMETER, value = "a0p0")
            })
    @Setter(AccessLevel.NONE)
    private String id;
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

    @Override
    public ObjectType type() {
        return ObjectType.PLAYER;
    }

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
        language = Language.RUS;
        status = PlayerStatus.TUTORIAL;
        additionalStatus = PlayerStatusExtended.LANGUAGE_CHOOSE;
        stats = new PlayerStats();
        stats.setUnassignedPoints(stats.getDefaultPoints() + 5);
    }

    // ================================================== END MAIN ================================================== //

    // ================================================= FINAL STATS ================================================ //

    public PlayerStats stats() {
        return stats;
    }

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
    public EnemyType getType() {
        return EnemyType.PLAYER;
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
    public String toString() {
        TranslationManager translation = ApplicationContextProvider.bean(TranslationManager.class);
        StringBuilder info = new StringBuilder();
        info.append(translation.getMessage("player.info.if-i-remember", language)).append(":")
                .append("\n*").append(this.nickname).append("*, ")
                .append(this.stats.getLevel()).append(" ")
                .append(translation.getMessage("player.info.level", language).toLowerCase())
                .append(" (").append(stats.getTotalStats() - stats.getAssignedPoints())
                .append("/").append(stats.getStatsToNextLevelUp()).append(")")
                .append(stats.getUnassignedPoints() > 0 ? " (+" + stats.getUnassignedPoints() + ")" : "");
        for (Stats stat : Stats.values()) {
            info.append("\n").append(this.stats().getInfoRow(stat, true, language, true));
        }
        info.append("\n\n_").append(translation.getMessage("player.info.what-else", language)).append("?_");
        return info.toString();
    }

    public String toStatString(int index) {
        return index + ". " + this.nickname + ", " + stats.getLevel() + " (" + stats.getTotalStats() + ")" + "\n";
    }

    public String getStatMenu(TranslationManager translation) {
        int unassigned = stats.getUnassignedPoints();
        StringBuilder info = new StringBuilder();
        info.append(nickname).append(", ")
                .append(stats.getLevel()).append(" ").append(translation.getMessage("player.info.level").toLowerCase())
                .append(" (+").append(unassigned).append(")").append("\n");
        for (Stats stat : Stats.values()) {
            info.append("\n").append(this.stats().getInfoRow(stat, false, language, false));
        }
        return info.toString();
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
        return comparingInt(Player::getLevel)
                .thenComparingInt(Player::getTotalStats)
                .reversed();
    }

    // ================================================ END SERVICE ================================================= //
}