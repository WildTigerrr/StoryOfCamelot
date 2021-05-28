package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.MoneyCalculation;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ActionType;
import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.service.ApplicationContextProvider;
import com.wildtigerrr.StoryOfCamelot.bin.service.ScheduledAction;
import com.wildtigerrr.StoryOfCamelot.bin.service.Time;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.CharacterStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.jetbrains.annotations.NotNull;

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
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Language language;
    @Enumerated(EnumType.STRING)
    private CharacterStatus status;
    @Enumerated(EnumType.STRING)
    private PlayerStatusExtended additionalStatus;
    private Double currentHealth;

    @Embedded
    private PlayerStats stats;

    // ------------------- GETTERS AND SETTERS ---------------------------------------------------------------------- //

    @Override
    public ObjectType type() {
        return ObjectType.PLAYER;
    }

    // ------------------- CONSTRUCTORS ----------------------------------------------------------------------------- //

    protected Player() {
    }

    public Player(String externalId, String nickname, Location location) {
        this.externalId = Objects.requireNonNull(externalId);
        this.nickname = nickname;
        this.location = location;
        status = CharacterStatus.TUTORIAL;
        additionalStatus = PlayerStatusExtended.LANGUAGE_CHOOSE;
        money = 10000L;
        currentHealth = 1.0;
        stats = new PlayerStats();
        stats.setUnassignedPoints(stats.getDefaultPoints() + 5);
    }

    // ================================================== END MAIN ================================================== //

    // ================================================= FINAL STATS ================================================ //

    public PlayerStats stats() {
        return stats;
    }

    private Integer speed;

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
        return getCurrentHealth() > 0;
    }

    @Override
    public void applyDamage(int damage) {
        setCurrentHealth(getCurrentHealth() - damage);
        PlayerServiceImpl.enableRegeneration(this);
    }

    @Override
    public EnemyType getType() {
        return EnemyType.PLAYER;
    }

    @Override
    public int getHealth() {
        return getCurrentHealth().intValue();
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

    @Setter(AccessLevel.NONE)
    private Long money;
    private Integer diamonds;

    public void addMoney(long money) {
        this.money += money;
    }

    public void addMoney(int money) {
        addMoney((long) money);
    }

    public void pay(long money) {
        this.money -= money;
        if (this.money < 0) throw new InvalidInputException(getId() + " not have enough money");
    }

    public void pay(int money) {
        pay((long) money);
    }

    // ================================================= END FINANCE ================================================ //

    // ================================================== MOVEMENT ================================================== //

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    public void moveTo(Location targetLocation) {
        setLocation(targetLocation);
    }

    // ================================================ END MOVEMENT ================================================ //

    @OneToMany(
            cascade = {CascadeType.ALL},
            mappedBy = "player"
    )
    private List<Backpack> backpacks = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "filelink_id")
    private FileLink imageLink;

    public Boolean isNew() {
        return getNickname().equals(getExternalId());
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
                .append(stats.getUnassignedPoints() > 0 ? " (+" + stats.getUnassignedPoints() + ") " : " ")
                .append(Stats.HEALTH.emoji()).append(getHealth()).append("/").append(stats().getHealth());
        for (Stats stat : Stats.values()) {
            info.append("\n").append(this.stats().getInfoRow(stat, true, language, true));
        }
        info.append("\n").append(MoneyCalculation.moneyOf(this.money, language));
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

    @Override
    public int compareTo(@NotNull Player p) {
        return getComparator().compare(this, p);
    }

    private static Comparator<Player> getComparator() {
        return comparingInt(Player::getLevel)
                .thenComparingInt(Player::getTotalStats)
                .reversed();
    }

    // ================================================ END SERVICE ================================================= //
}
