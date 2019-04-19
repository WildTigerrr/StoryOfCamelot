package com.wildtigerrr.StoryOfCamelot.database.schema;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String external_id;
    private String nickname;
    private Boolean isNew;
    private Integer level;

    protected Player() {
    }

    public Player(String externalId, String nickname) {
        this.external_id = externalId;
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
        return external_id;
    }

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
        return nickname.replaceAll("[^a-zA-Z0-9\\s]", "");
    }
}
