package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;

@Entity
@Table(name = "npc")
public class Npc {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
}
