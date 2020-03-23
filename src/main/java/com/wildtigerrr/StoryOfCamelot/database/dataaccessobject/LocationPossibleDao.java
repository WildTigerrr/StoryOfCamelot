package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.LocationPossible;
import org.springframework.data.repository.CrudRepository;

public interface LocationPossibleDao extends CrudRepository<LocationPossible, String> {
}
