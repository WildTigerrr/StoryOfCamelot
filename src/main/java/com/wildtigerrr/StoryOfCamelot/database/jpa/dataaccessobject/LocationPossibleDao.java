package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationPossible;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LocationPossibleDao extends CrudRepository<LocationPossible, String> {
    List<LocationPossible> findAllByLocation(Location location);
}
