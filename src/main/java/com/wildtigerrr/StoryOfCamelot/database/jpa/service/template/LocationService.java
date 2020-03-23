package com.wildtigerrr.StoryOfCamelot.database.jpa.service.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface LocationService {
    Location create(Location location);
    HashMap<String, Location> create(ArrayList<Location> locations);
    void delete(String id);
    Location findById(String id);
    Location findByName(String locationName);
    Location update(Location location);
    List<Location> getAll();
    HashMap<String, Location> getAllAsMap();
}