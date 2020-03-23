package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Npc;
import org.springframework.data.repository.CrudRepository;

public interface NpcDao extends CrudRepository<Npc, String> {
}
