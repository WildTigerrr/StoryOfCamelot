package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;

@Entity
@Table(name = "possible_location")
public class PossibleLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    public Integer getId() {
        return id;
    }

    public Mob getMob() {
        return mob;
    }

    public Location getLocation() {
        return location;
    }

}
