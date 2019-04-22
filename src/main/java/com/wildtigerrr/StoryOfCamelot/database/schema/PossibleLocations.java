package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;

@Entity
@Table(name = "possible_locations")
public class PossibleLocations {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "mob_id")
    private Mob mob;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    protected PossibleLocations() {
    }

    public PossibleLocations(Mob mob, Location location) {
        this.mob = mob;
        this.location = location;
    }

    public Mob getMob() {
        return mob;
    }

    public Location getLocation() {
        return location;
    }

}
