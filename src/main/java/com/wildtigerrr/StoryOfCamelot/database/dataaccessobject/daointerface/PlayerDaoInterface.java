package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.daointerface;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerDaoInterface {
    /*    @Query("SELECT p FROM Player p WHERE p.external_id = :external_id")
    Player findByExternalId(@Param("external_id") int externalId);

    @Query("SELECT p FROM Player p WHERE p.nickname = :nickname")
    Player findByNickname(@Param("nickname") String nickname);*/
    Player addPlayer(Player player);
    void delete(int id);
    Player getByExternalId(int externalId);
    Player getById(int playerId);
    Player getByNickname(String nickname);
    Player editPlayer(Player player);
    List<Player> getAll();
}
