package com.wildtigerrr.StoryOfCamelot.database.jpa.service.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationPossible;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;

import java.util.ArrayList;
import java.util.List;

public interface LocationPossibleService {
    LocationPossible create(LocationPossible possibleLocation);
    void create(ArrayList<LocationPossible> possibleLocations);
    void delete(String id);
    LocationPossible update(LocationPossible possibleLocation);
    List<LocationPossible> getAll();
    List<LocationPossible> getPossibleMobs(Location location);
}
