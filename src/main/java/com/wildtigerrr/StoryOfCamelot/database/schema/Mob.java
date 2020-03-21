package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.MobTemplate;
import com.wildtigerrr.StoryOfCamelot.database.interfaces.Fighter;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mob")
@Getter @Setter
public class Mob implements Fighter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Integer id;
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
    private List<PossibleLocation> possibleLocations = new ArrayList<>();
    @ManyToOne(optional = true)
    @JoinColumn(name = "filelink_id")
    private FileLink imageLink;

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

    public Mob addPossibleLocation(PossibleLocation possibleLocation) {
        this.possibleLocations.add(possibleLocation);
        return this;
    }

    public void removePossibleLocation(PossibleLocation possibleLocation) {
        this.possibleLocations.remove(possibleLocation);
    }

}
