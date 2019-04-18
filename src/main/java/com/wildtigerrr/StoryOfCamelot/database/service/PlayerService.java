package com.wildtigerrr.StoryOfCamelot.database.service;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;

import java.util.List;

public interface PlayerService {
    Player addPlayer(Player player);
    void delete(int id);
    Player getByExternalId(int externalId);
    Player getByNickname(String nickname);
    Player editPlayer(Player player);
    List<Player> getAll();
}
