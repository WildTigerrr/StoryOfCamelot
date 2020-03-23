package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import org.springframework.data.repository.CrudRepository;

public interface LocationDao extends CrudRepository<Location, String> {

    Location findBySystemName(String name);

}