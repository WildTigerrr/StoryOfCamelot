package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.Npc;
import org.springframework.data.repository.CrudRepository;

public interface NpcDao extends CrudRepository<Npc, String> {
}
