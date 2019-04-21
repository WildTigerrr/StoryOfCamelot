package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;

import java.util.List;

public interface PlayerService {
    Player create(Player player);
    void delete(int id);
    Player findByExternalId(String externalId);
    Player findByNickname(String nickname);
    Player update(Player player);
    List<Player> getAll();
    String getPlayerInfo(String externalId);
}
