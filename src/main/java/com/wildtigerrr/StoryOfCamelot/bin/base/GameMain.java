package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.BattleHandler;
import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.GameSettings;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.MobServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
@Service
public class GameMain {

    private final ResponseManager messages;
    private final PlayerServiceImpl playerService;
    private final LocationServiceImpl locationService;
    private final TranslationManager translation;
    private final MobServiceImpl mobService;
    private final BattleHandler battleHandler;

    @Autowired
    public GameMain(
            ResponseManager messages,
            PlayerServiceImpl playerService,
            LocationServiceImpl locationService,
            TranslationManager translation,
            MobServiceImpl mobService,
            BattleHandler battleHandler
    ) {
        this.messages = messages;
        this.playerService = playerService;
        this.locationService = locationService;
        this.translation = translation;
        this.mobService = mobService;
        this.battleHandler = battleHandler;
    }

    public void getTopPlayers(String userId) {
        List<Player> players = playerService.getAll();
        players = players.stream()
                .sorted()
                .collect(Collectors.toList());
        if (players.size() > 10)
            players = players.subList(players.size() - 10, players.size());
        AtomicInteger index = new AtomicInteger();
        String top = "Топ игроков: \n\n" +
                players.stream()
                        .map(pl -> pl.toStatString(index.incrementAndGet()))
                        .collect(Collectors.joining());
        messages.sendMessage(
                top,
                userId
        );
    }

    public void sendLanguageSelector(String userId, Language lang) {// translation.get(lang).languageSelectPrompt(),
        messages.sendMessage(
                translation.getMessage("tutorial.lang.choose", lang),
                KeyboardManager.getKeyboardForLanguageSelect(),
                userId
        );
    }

    public Player getPlayer(String externalId) {
        Player player;
        player = playerService.findByExternalId(externalId);
        if (player == null) {
            player = new Player(externalId, externalId, locationService.findByName(GameSettings.DEFAULT_LOCATION.get()));
            player = playerService.create(player);
        }
        return player;
    }

    public void setNickname(Player player, String newName) {
        String message;
        if (Player.containsSpecialCharacters(newName)) {
            message = translation.getMessage("player.nickname.wrong-symbols", player);
        } else if (!player.setNickname(newName)) {
            message = translation.getMessage("player.nickname.too-long", player,
                    new Object[]{String.valueOf(Player.getNicknameLengthMax())});
        } else if (player.getNickname().isEmpty()) {
            message = translation.getMessage("player.nickname.empty", player);
        } else if (playerService.findByNickname(player.getNickname()) != null) {
            message = translation.getMessage("player.nickname.duplicate", player);
        } else if (player.getAdditionalStatus() == PlayerStatusExtended.TUTORIAL_NICKNAME) {
            return;
        } else {
            playerService.update(player);
            message = translation.getMessage("player.nickname.accept", player,
                    new Object[]{player.getNickname()});
        }
        messages.sendMessage(message, player.getExternalId(), true);
    }

    public Player addExperience(Player player, Stats stat, int experience, Boolean sendExperienceGet) {
        try {
            List<String> eventList = player.addStatExp(
                    experience,
                    stat,
                    player.getLanguage(),
                    translation
            );
            if (eventList != null && !eventList.isEmpty()) {
                for (String event : eventList) {
                    if (event != null && !event.equals("")) {
                        messages.sendMessage(event, player.getExternalId());
                    }
                }
            }
            if (sendExperienceGet) {
                messages.sendMessage(
                        translation.getMessage("player.stats.experience-taken", player.getLanguage(),
                                new Object[]{experience}),
                        player.getExternalId()
                );
            }
            return player;
        } catch (InvalidInputException e) {
            messages.sendErrorReport(e);
        }
        return player;
    }

    public void sendSkillWindow(Player player) {
        messages.sendMessage(player.getStatMenu(translation), KeyboardManager.getKeyboardForStatUp(player.getUnassignedPoints()), player.getExternalId());
    }

    public void statUp(UpdateWrapper message) {
        String[] commandParts = message.getText().split("_", 3);
        if (commandParts.length == 3 && commandParts[1].length() == 1 && StringUtils.isNumeric(commandParts[2])) {
            Stats stat = Stats.getStat(commandParts[1]);
            if (stat == null) {
                messages.sendMessage(translation.getMessage("player.stats.invalid", message), message.getUserId());
            } else {
                Player player = message.getPlayer();
                String result = player.raiseStat(stat, Integer.valueOf(commandParts[2]), player.getLanguage(), translation);
                if (!result.equals(translation.getMessage("player.stats.invalid", message)))
                    playerService.update(player);
                messages.sendAnswer(message.getQueryId(), result);
                if (player.getUnassignedPoints() == 0) {
                    messages.sendMessageEdit(message.getMessageId(), player.getStatMenu(translation), player.getExternalId(), false);
                } else {
                    messages.sendMessageEdit(
                            message.getMessageId(),
                            player.getStatMenu(translation),
                            KeyboardManager.getKeyboardForStatUp(player.getUnassignedPoints()),
                            player.getExternalId(),
                            false
                    );
                }
            }
        } else {
            messages.sendMessage(translation.getMessage("commands.invalid", message), message.getUserId());
        }
    }

    public void fight(UpdateWrapper message) {
        messages.sendMessage(translation.getMessage("battle.start", message), message.getUserId());
        Mob mob = mobService.getAll().get(0);

        List<String> battleLog = battleHandler.fight(message.getPlayer(), mob, message.getPlayer().getLanguage());
        StringBuilder history = new StringBuilder();
        for (String logRow : battleLog) {
            history.append(logRow).append("\n");
        }
        messages.sendMessage(history.toString(), message.getUserId());

        // TODO Allow actions by statuses (class to compare)
        // TODO New status (new table?) with current situation
    }

}
