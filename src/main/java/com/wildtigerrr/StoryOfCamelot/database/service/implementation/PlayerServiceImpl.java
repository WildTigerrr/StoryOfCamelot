package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.PlayerDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.service.template.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private TranslationManager translation;

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
    public void create(ArrayList<Player> players) {
        playerDao.saveAll(players);
    }

    @Override
    public void delete(int id) {
        playerDao.findById(id).ifPresent(player -> playerDao.delete(player));
    }

    @Override
    public Player findById(int id) {
        Optional obj = playerDao.findById(id);
        if (obj.isPresent()) {
            return (Player) obj.get();
        }
        return null;
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
    public String getPlayerInfo(String externalId, Language lang) {
        Player player = findByExternalId(externalId);
        if (player != null) {
            return player.toString();
        } else {
            return translation.get(lang).playerNotExist(); // MainText.PLAYER_NOT_EXIST.text(Language.RUS);
        }
    }
}
