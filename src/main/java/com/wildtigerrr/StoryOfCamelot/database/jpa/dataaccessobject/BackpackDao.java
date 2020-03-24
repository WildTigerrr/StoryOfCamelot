package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BackpackDao extends CrudRepository<Backpack, String> {
    Optional<Backpack> findByPlayer_Id(String playerId);
}
