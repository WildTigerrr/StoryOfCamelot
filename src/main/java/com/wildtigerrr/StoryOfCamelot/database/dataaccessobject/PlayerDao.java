package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PlayerDao extends CrudRepository<Player, String> {

    @Query("SELECT p FROM Player p WHERE p.externalId = :externalId")
    Player findByExternalId(@Param("externalId") String externalId);

    Player findByNickname(String nickname);

}