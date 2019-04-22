package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.PossibleLocation;

import java.util.ArrayList;
import java.util.List;

public interface PossibleLocationService {
    PossibleLocation create(PossibleLocation possibleLocation);
    void create(ArrayList<PossibleLocation> possibleLocations);
    void delete(int id);
    PossibleLocation update(PossibleLocation possibleLocation);
    List<PossibleLocation> getAll();
}
