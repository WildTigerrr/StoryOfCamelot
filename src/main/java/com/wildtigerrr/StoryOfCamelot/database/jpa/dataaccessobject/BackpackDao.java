package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.BackpackType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BackpackDao extends CrudRepository<Backpack, String> {
    Optional<Backpack> findByPlayer_Id(String playerId);
    Optional<Backpack> findByPlayerIdAndType(String playerId, BackpackType type);
}
