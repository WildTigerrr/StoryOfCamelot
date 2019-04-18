package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

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
    public Player addPlayer(Player player) {
        return playerDao.saveAndFlush(player);
    }

    @Override
    public void delete(int id) {
        playerDao.deleteById(id);
    }

    @Override
    public Player getByExternalId(int externalId) {
        return playerDao.findByExternalId(externalId);
    }

    @Override
    public Player getByNickname(String nickname) {
        return playerDao.findByNickname(nickname);
    }

    @Override
    public Player editPlayer(Player player) {
        return playerDao.saveAndFlush(player);
    }

    @Override
    public List<Player> getAll() {
        return playerDao.findAll();
    }
}
