package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;

import java.util.ArrayList;
import java.util.List;

public interface PlayerService {
    Player create(Player player);
    void create(ArrayList<Player> players);
    void delete(int id);
    Player findById(int id);
    Player findByExternalId(String externalId);
    Player findByNickname(String nickname);
    Player update(Player player);
    List<Player> getAll();
    String getPlayerInfo(String externalId);
}
