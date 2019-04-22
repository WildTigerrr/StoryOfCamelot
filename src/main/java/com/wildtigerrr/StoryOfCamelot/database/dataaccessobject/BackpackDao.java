package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.Backpack;
import org.springframework.data.repository.CrudRepository;

public interface BackpackDao extends CrudRepository<Backpack, Integer> {
}
