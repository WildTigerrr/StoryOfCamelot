package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.FileLink;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;

import java.util.ArrayList;
import java.util.List;

public interface LocationService {
    Location create(Location location);
    void create(ArrayList<Location> locations);
    void delete(int id);
    Location findById(int id);
    Location update(Location location);
    List<Location> getAll();
}
