package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.PossibleLocations;
import org.springframework.data.repository.CrudRepository;

public interface PossibleLocationsDao extends CrudRepository<PossibleLocations, Integer> {
}
