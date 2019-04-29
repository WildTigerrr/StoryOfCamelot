package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.LocationNear;
import org.springframework.data.repository.CrudRepository;

public interface LocationNearDao extends CrudRepository<LocationNear, Integer> {
}
