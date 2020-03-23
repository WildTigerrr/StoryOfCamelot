package com.wildtigerrr.StoryOfCamelot.database.jpa.service.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationNear;

import java.util.ArrayList;

public interface LocationNearService {
    LocationNear create(LocationNear locationNear);
    void create(ArrayList<LocationNear> locationNear);
    void delete(String id);
    LocationNear update(LocationNear locationNear);
    ArrayList<Location> getNearLocations(Location location);
    int getDistance(Location start, Location finish);
    ArrayList<LocationNear> getAll();
}