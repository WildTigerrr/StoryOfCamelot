package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.bin.MainText;
import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.PlayerDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerDao playerDao;

    @Override
    public synchronized Player create(Player player) {
        Player existingPlayer = playerDao.findByExternalId(player.getExternalId());
        if (existingPlayer != null) {
            return existingPlayer;
        } else {
            return playerDao.save(player);
        }
    }

    @Override
    public void delete(int id) {
        playerDao.findById(id).ifPresent(player -> playerDao.delete(player));
    }

    @Override
    public Player findByExternalId(String externalId) {
        return playerDao.findByExternalId(externalId);
    }

    @Override
    public Player findByNickname(String nickname) {
        return playerDao.findByNickname(nickname);
    }

    @Override
    public synchronized Player update(Player player) {
        return playerDao.save(player);
    }

    @Override
    public List<Player> getAll() {
        return (List<Player>) playerDao.findAll();
    }

    @Override
    public String getPlayerInfo(String externalId) {
        Player player = findByExternalId(externalId);
        if (player != null) {
            return player.toString();
        } else {
            return MainText.PLAYER_NOT_EXIST.text();
        }
    }
}
