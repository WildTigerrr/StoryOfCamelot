package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.MobDrop;
import org.springframework.data.repository.CrudRepository;

public interface MobDropDao extends CrudRepository<MobDrop, String> {
}
