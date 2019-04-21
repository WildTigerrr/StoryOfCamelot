package com.wildtigerrr.StoryOfCamelot.database.schema;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "PLAYER")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String external_id;
    private String nickname;
    private Integer level;
    private Integer experience;
    private Integer hitpoints;
    private Integer hitpoints_max;
    private Integer damage;
    private Integer agility;
    private String status;
    private Integer speed;
    private Boolean is_new;

    protected Player() {
    }

    public Player(String externalId, String nickname) {
        this.external_id = externalId;
        this.nickname = nickname;
        this.is_new = externalId.equals(nickname);
    }

    public Boolean isNew() {
        return is_new;
    }
    public void setup() {
        this.is_new = false;
        this.level = 1;
    }

    public Integer getId() {
        return id;
    }

    public String getExternalId() {
        return external_id;
    }

    // TODO admin method for setting
    public void setExternalId(String externalId) {
        this.external_id = externalId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = removeSpecialCharacters(nickname);
    }

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
