package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.bin.enums.GameSettings;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.PlayerDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.service.template.LocationService;
import com.wildtigerrr.StoryOfCamelot.database.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
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
    public List<Player> getTopPlayers(int count) {
        List<Player> players = getAll();
        players = players.stream()
                .sorted()
                .collect(Collectors.toList());
        if (players.size() > count)
            players = players.subList(players.size() - count, players.size());
        return players;
    }

    @Override
    public void sendTopPlayers(String userId) {
        List<Player> players = getTopPlayers(10);
        AtomicInteger index = new AtomicInteger();
        String top = "Топ игроков: \n\n" +
                players.stream()
                        .map(pl -> pl.toStatString(index.incrementAndGet()))
                        .collect(Collectors.joining());
        messages.sendMessage(TextResponseMessage.builder()
                .text(top).targetId(userId).build()
        );
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
        Player player;
        player = findByExternalId(externalId);
        if (player == null) {
            player = new Player(externalId, externalId, locationService.findByName(GameSettings.DEFAULT_LOCATION.get()));
            player = create(player);
        }
        return player;
    }

    @Override
    public void setNickname(Player player, String newName) {
        String message;
        if (Player.containsSpecialCharacters(newName)) {
            message = translation.getMessage("player.nickname.wrong-symbols", player);
        } else if (!player.setNickname(newName)) {
            message = translation.getMessage("player.nickname.too-long", player,
                    new Object[]{String.valueOf(Player.getNicknameLengthMax())});
        } else if (player.getNickname().isEmpty()) {
            message = translation.getMessage("player.nickname.empty", player);
        } else if (findByNickname(player.getNickname()) != null) {
            message = translation.getMessage("player.nickname.duplicate", player);
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

    @Override
    public void sendPlayerInfo(UpdateWrapper message) {
        /*getPlayerInfo(
                message.getUserId(),
                message.getPlayer().getLanguage()
        )*/
        messages.sendMessage(TextResponseMessage.builder()
                .text(message.getPlayer().toString())
                .targetId(message)
                .applyMarkup(true).build()
        );
    }

}
