package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemDao extends CrudRepository<Item, Integer> {
}
