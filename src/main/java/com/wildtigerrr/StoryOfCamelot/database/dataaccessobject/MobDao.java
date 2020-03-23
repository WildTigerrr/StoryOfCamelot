package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.Mob;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MobDao extends CrudRepository<Mob, String> {

    Optional<Mob> findBySystemName(String name);

}