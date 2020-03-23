package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationPossible;
import org.springframework.data.repository.CrudRepository;

public interface LocationPossibleDao extends CrudRepository<LocationPossible, String> {
}
