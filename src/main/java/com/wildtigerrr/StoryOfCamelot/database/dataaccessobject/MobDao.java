package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.Mob;
import org.springframework.data.repository.CrudRepository;

public interface MobDao extends CrudRepository<Mob, Integer> {
}