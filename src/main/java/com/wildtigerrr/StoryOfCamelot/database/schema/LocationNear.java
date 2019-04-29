package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;

public class LocationNear {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    public Integer getId() {
        return id;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getFinishLocation() {
        return finishLocation;
    }

    public void setFinishLocation(Location finishLocation) {
        this.finishLocation = finishLocation;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

}