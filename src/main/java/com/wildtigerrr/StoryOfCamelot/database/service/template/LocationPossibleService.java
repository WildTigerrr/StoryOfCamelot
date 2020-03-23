package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.LocationPossible;

import java.util.ArrayList;
import java.util.List;

public interface LocationPossibleService {
    LocationPossible create(LocationPossible possibleLocation);
    void create(ArrayList<LocationPossible> possibleLocations);
    void delete(String id);
    LocationPossible update(LocationPossible possibleLocation);
    List<LocationPossible> getAll();
}
