package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.MobDrop;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MobDropDao extends CrudRepository<MobDrop, String> {
    List<MobDrop> findByMobId(String mobId);
}