package com.wildtigerrr.StoryOfCamelot.database.schema;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String externalId;
    private String nickname;
    private Integer level;
    private Integer experience;
    private Integer hitpoints;
    private Integer hitpointsMax;
    private Integer damage;
    private Integer agility;
    private String status;
    private Integer speed;
    private Boolean isNew;

    @OneToMany(
            cascade = {CascadeType.ALL},
            mappedBy = "player"
    )
    private List<Backpack> backpacks = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "filelink_id")
    private FileLink imageLink;

    protected Player() {
    }

    public Player(String externalId, String nickname) {
        this.externalId = externalId;
        this.nickname = nickname;
        this.isNew = externalId.equals(nickname);
    }

    public Boolean isNew() {
        return isNew;
    }
    public void setup() {
        this.isNew = false;
        this.level = 1;
    }

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
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

    public Integer getAgility() {
        return agility;
    }

    public void setAgility(Integer agility) {
        this.agility = agility;
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

    @Override
    public String toString() {
        return "Если память тебя не подводит, то:"
                + "\n*" + this.nickname + "*, " + this.level + " уровень"
                + "\n\n_Что же ещё известно?_";
    }

    private String removeSpecialCharacters(String nickname) {
        nickname = nickname.replaceAll("[^a-zA-Z\\s]", "");
        if (nickname.length() > 25) nickname = nickname.substring(0, 35);
        return nickname;
    }
}
