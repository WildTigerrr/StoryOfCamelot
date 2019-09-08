package com.wildtigerrr.StoryOfCamelot.database.schema;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "location_near")
@Getter @Setter
public class LocationNear {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Integer id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "startLocation_id", nullable = false)
    private Location startLocation;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "finishLocation_id", nullable = false)
    private Location finishLocation;

    private int distance;

    protected LocationNear() {
    }

    public LocationNear(Location start, Location finish, int distance) {
        this.startLocation = start;
        this.finishLocation = finish;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "LocationNear{" +
                "id=" + id +
                ", finishLocation=" + finishLocation.getSystemName() +
                ", distance=" + distance +
                '}';
    }
}