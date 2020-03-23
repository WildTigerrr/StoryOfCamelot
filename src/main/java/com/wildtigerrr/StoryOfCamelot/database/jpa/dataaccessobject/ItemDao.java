package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemDao extends CrudRepository<Item, String> {
}
