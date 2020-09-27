package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Store;
import org.springframework.data.repository.CrudRepository;

public interface StoreDao extends CrudRepository<Store, String> {
}
