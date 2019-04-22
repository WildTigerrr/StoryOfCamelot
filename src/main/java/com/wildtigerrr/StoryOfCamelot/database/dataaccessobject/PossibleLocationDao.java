package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.PossibleLocation;
import org.springframework.data.repository.CrudRepository;

public interface PossibleLocationDao extends CrudRepository<PossibleLocation, Integer> {
}
