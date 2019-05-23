package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.MobTemplate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mob")
public class Mob {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String systemName;
    @Enumerated(EnumType.STRING)
    private NameTranslation name;
    private Integer level;
    private Integer damage;
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

    public Integer getId() {
        return id;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getName(Language lang) {
        return name.getName(lang);
    }

    public void setName(NameTranslation name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public Integer getHitpointsMax() {
        return hitpointsMax;
    }

    public void setHitpointsMax(Integer hitpointsMax) {
        this.hitpointsMax = hitpointsMax;
    }

    public Integer getDefence() {
        return defence;
    }

    public void setDefence(Integer defence) {
        this.defence = defence;
    }

    public Integer getAgility() {
        return agility;
    }

    public void setAgility(Integer agility) {
        this.agility = agility;
    }

    public List<PossibleLocation> getPossibleLocations() {
        return possibleLocations;
    }

    public void setPossibleLocations(List<PossibleLocation> possibleLocations) {
        this.possibleLocations = possibleLocations;
    }

    public Mob addPossibleLocation(PossibleLocation possibleLocation) {
        this.possibleLocations.add(possibleLocation);
        return this;
    }

    public void removePossibleLocation(PossibleLocation possibleLocation) {
        this.possibleLocations.remove(possibleLocation);
    }

    public FileLink getImageLink() {
        return imageLink;
    }

    public void setImageLink(FileLink imageLink) {
        this.imageLink = imageLink;
    }
}
