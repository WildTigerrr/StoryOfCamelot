package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import org.springframework.data.repository.CrudRepository;

public interface BackpackDao extends CrudRepository<Backpack, String> {
}
