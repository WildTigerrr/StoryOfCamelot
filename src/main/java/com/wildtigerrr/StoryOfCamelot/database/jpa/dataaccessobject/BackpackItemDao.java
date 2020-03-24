package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.BackpackItem;
import org.springframework.data.repository.CrudRepository;

public interface BackpackItemDao extends CrudRepository<BackpackItem, String> {
}
