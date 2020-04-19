package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.bin.enums.GameSettings;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.PlayerDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.LocationService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final ResponseManager messages;
    private final PlayerDao playerDao;
    private final TranslationManager translation;
    private final LocationService locationService;

    @Autowired
    public PlayerServiceImpl(ResponseManager messages, PlayerDao playerDao, TranslationManager translation, LocationService locationService) {
        this.messages = messages;
        this.playerDao = playerDao;
        this.translation = translation;
        this.locationService = locationService;
    }

    @Override
    public synchronized Player createIfNotExist(Player player) {
        Player existingPlayer = playerDao.findByExternalId(player.getExternalId());
        return Objects.requireNonNullElseGet(existingPlayer, () -> save(player));
    }

    private synchronized Player save(Player player) {
        return playerDao.save(player);
    }

    @Override
    public void create(ArrayList<Player> players) {
        playerDao.saveAll(players);
    }

    @Override
    public void delete(String id) {
        playerDao.findById(id).ifPresent(playerDao::delete);
    }

    @Override
    public Player findById(String id) {
        Optional<Player> obj = playerDao.findById(id);
        return obj.orElse(null);
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
    public List<Player> getTopPlayers(int count) {
        List<Player> players = getAll();
        players = players.stream()
                .sorted()
                .collect(Collectors.toList());
        if (players.size() > count)
            players = players.subList(0, count);
        return players;
    }

    @Override
    public String getPlayerInfo(String externalId, Language lang) {
        Player player = findByExternalId(externalId);
        if (player != null) {
            return player.toString();
        } else {
            return translation.getMessage("player.info.not-exist", lang);
        }
    }

    @Override
    public Player getPlayer(String externalId) {
        Player player = findByExternalId(externalId);
        if (player == null) {
            player = new Player(externalId, externalId, locationService.findByName(GameSettings.DEFAULT_LOCATION.get()));
            player = save(player);
        }
        return player;
    }

    @Override
    public void setNickname(Player player, String newName) {
        String message;
        if (Player.containsSpecialCharacters(newName)) {
            message = translation.getMessage("player.nickname.wrong-symbols", player);
        } else if (findByNickname(newName) != null) {
            message = translation.getMessage("player.nickname.duplicate", player, new Object[]{newName});
        } else if (!player.setNickname(newName)) {
            message = translation.getMessage("player.nickname.too-long", player,
                    new Object[]{String.valueOf(Player.getNicknameLengthMax())});
        } else if (player.getNickname().isEmpty()) {
            message = translation.getMessage("player.nickname.empty", player);
        } else if (player.getAdditionalStatus() == PlayerStatusExtended.TUTORIAL_NICKNAME) {
            return;
        } else {
            update(player);
            message = translation.getMessage("player.nickname.accept", player,
                    new Object[]{player.getNickname()});
        }
        messages.sendMessage(TextResponseMessage.builder()
                .text(message).targetId(player).applyMarkup(true).build()
        );
    }

}
