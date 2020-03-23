package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;

import java.util.ArrayList;
import java.util.List;

public interface PlayerService {
    Player createIfNotExist(Player player);
    void create(ArrayList<Player> players);
    void delete(String id);
    Player findById(String id);
    Player findByExternalId(String externalId);
    Player findByNickname(String nickname);
    Player update(Player player);
    List<Player> getAll();
    List<Player> getTopPlayers(int count);
    String getPlayerInfo(String externalId, Language lang);
    Player getPlayer(String externalId);
    void setNickname(Player player, String newName);
}
