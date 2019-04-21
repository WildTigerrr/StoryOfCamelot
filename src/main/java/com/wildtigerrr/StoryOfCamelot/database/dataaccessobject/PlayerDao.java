package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PlayerDao extends CrudRepository<Player, Integer> {

    @Query("SELECT p FROM Player p WHERE p.external_id = :external_id")
    Player findByExternalId(@Param("external_id") String externalId);

    Player findByNickname(String nickname);

/*    @Query("SELECT p FROM Player p WHERE p.nickname = :nickname")
    Player findByNickname(@Param("nickname") String nickname);*/

}
