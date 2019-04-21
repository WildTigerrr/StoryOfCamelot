package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import org.springframework.data.repository.CrudRepository;

public interface LocationDao extends CrudRepository<Location, Integer> {
}
