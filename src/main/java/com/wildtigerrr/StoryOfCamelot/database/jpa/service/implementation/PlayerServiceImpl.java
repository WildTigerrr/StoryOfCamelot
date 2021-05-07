package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ActionType;
import com.wildtigerrr.StoryOfCamelot.bin.enums.GameSettings;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.service.ScheduledAction;
import com.wildtigerrr.StoryOfCamelot.bin.service.Time;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.PlayerDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.LocationService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
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
    @Async
    public void heal(String playerId, String quantity) {
        Player player = findById(playerId);
        if (player != null) {
            player.setCurrentHealth(Math.min(player.getCurrentHealth() + Double.parseDouble(quantity), player.stats().getHealth().doubleValue()));
            log.debug("Healed " + player.getNickname() + " to: " + player.getCurrentHealth());
            update(player);
            enableRegeneration(player);
        }
    }

    public static void enableRegeneration(Player player) {
        if (player.getHealth() < player.stats().getHealth()) {
            TimeDependentActions.scheduleAction(
                    new ScheduledAction(
                            Time.minutes(1), ActionType.REGENERATION, player.getId(), String.valueOf(player.stats().getHealth() / 10.0)
                    ), false
            );
        }
    }

}
