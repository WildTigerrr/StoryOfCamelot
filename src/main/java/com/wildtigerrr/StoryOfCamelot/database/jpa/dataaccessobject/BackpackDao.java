package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.BackpackType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BackpackDao extends CrudRepository<Backpack, String> {
    Optional<Backpack> findByPlayerId(String playerId);
    @Query("SELECT b FROM Backpack b JOIN FETCH b.items WHERE b.player.id = :playerId AND b.type = :type")
    Optional<Backpack> findByPlayerIdAndType(String playerId, BackpackType type);
}
