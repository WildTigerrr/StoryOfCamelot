package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.MobTemplate;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mob")
@Getter @Setter
public class Mob extends SimpleObject implements Fighter {

    @Id
    @SequenceGenerator(name = "mob_seq", sequenceName = "mob_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mob_seq")
    @GenericGenerator(
            name = "mob_seq",
            strategy = "com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = IdGenerator.VALUE_PREFIX_PARAMETER, value = "a0m0")
            })
    @Setter(AccessLevel.NONE)
    private String id;
    private String systemName;
    @Enumerated(EnumType.STRING)
    private NameTranslation name;
    private Integer level;
    private Integer damage;
    private Integer hitpoints;
    private Integer hitpointsMax;
    private Integer defence;
    private Integer agility;

    @OneToMany(
            cascade = {CascadeType.ALL},
            mappedBy = "mob"
    )
    private List<LocationPossible> possibleLocations = new ArrayList<>();
    @ManyToOne(optional = true)
    @JoinColumn(name = "filelink_id")
    private FileLink imageLink;

    @Override
    public ObjectType type() {
        return ObjectType.MOB;
    }

    protected Mob() {
    }

    public Mob(MobTemplate template) {
        this(
                template.name(),
                template.getName(),
                template.getLevel(),
                template.getDamage(),
                template.getHitpoints(),
                template.getDefence(),
                template.getAgility(),
                template.getFileLink()
        );
    }

    public Mob(String systemName, NameTranslation name, Integer level, Integer damage, Integer hitpointsMax, Integer defence, Integer agility, FileLink imageLink) {
        this.systemName = systemName;
        this.name = name;
        this.level = level;
        this.damage = damage;
        this.hitpointsMax = hitpointsMax;
        this.defence = defence;
        this.agility = agility;
        this.imageLink = imageLink;
    }

    public String getName(Language lang) {
        return name.getName(lang);
    }

    public String getName(Player player) {
        return getName(player.getLanguage());
    }

    public String getName(UpdateWrapper update) {
        return getName(update.getPlayer());
    }

    @Override
    public boolean isAlive() {
        if (hitpoints == null) hitpoints = hitpointsMax;
        return hitpoints > 0;
    }

    @Override
    public void applyDamage(int damage) {
        setHitpoints(hitpoints -= damage);
    }

    @Override
    public EnemyType getType() {
        return EnemyType.MOB;
    }

    @Override
    public int getHealth() {
        return getHitpoints();
    }

    @Override
    public int getDefence() {
        return this.defence;
    }

    @Override
    public int getDamage() {
        return this.damage;
    }

    public Mob addPossibleLocation(LocationPossible possibleLocation) {
        this.possibleLocations.add(possibleLocation);
        return this;
    }

    public void removePossibleLocation(LocationPossible possibleLocation) {
        this.possibleLocations.remove(possibleLocation);
    }

}
