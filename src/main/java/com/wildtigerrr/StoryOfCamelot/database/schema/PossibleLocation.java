package com.wildtigerrr.StoryOfCamelot.database.schema;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "possible_location")
@Getter
public class PossibleLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "mob_id")
    private Mob mob;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    protected PossibleLocation() {
    }

    public PossibleLocation(Mob mob, Location location) {
        this.mob = mob;
        this.location = location;
    }

}
