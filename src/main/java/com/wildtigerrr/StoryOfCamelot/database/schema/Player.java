package com.wildtigerrr.StoryOfCamelot.database.schema;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "external_id")
    private String externalId;

    private String nickname;

    protected Player() {
    }

    public Player(String externalId, String nickname) {
        this.externalId = externalId;
        this.nickname = nickname;
    }

    public Integer getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
