package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MobDao extends CrudRepository<Mob, String> {

    Optional<Mob> findBySystemName(String name);

}