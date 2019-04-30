package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.LocationNear;

import java.util.ArrayList;
import java.util.List;

public interface LocationNearService {
    LocationNear create(LocationNear locationNear);
    void create(ArrayList<LocationNear> locationNear);
    void delete(int id);
    LocationNear update(LocationNear locationNear);
    ArrayList<Location> getNearLocations(Location location);
    ArrayList<LocationNear> getAll();
}