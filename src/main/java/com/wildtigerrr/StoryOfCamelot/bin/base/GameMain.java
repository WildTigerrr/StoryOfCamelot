package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.GameSettings;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.exceptions.SOCInvalidDataException;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class GameMain {

    @Autowired
    private ResponseManager messages;
    @Autowired
    private GameTutorial tutorial;
    @Autowired
    private PlayerServiceImpl playerService;
    @Autowired
    private LocationServiceImpl locationService;
    @Autowired
    private TranslationManager translation;

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

    public void sendLanguageSelector(String userId, Language lang) {
        messages.sendMessage(
                translation.get(lang).languageSelectPrompt(),
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
            message = translation.get(player.getLanguage()).nicknameWrongSymbols();
        } else if (!player.setNickname(newName)) {
            message = translation.get(player.getLanguage()).nicknameTooLong(String.valueOf(Player.getNicknameLengthMax()));
        } else if (player.getNickname().isEmpty()) {
            message = translation.get(player.getLanguage()).nicknameEmpty();
        } else if (playerService.findByNickname(player.getNickname()) != null) {
            message = translation.get(player.getLanguage()).nicknameDuplicate(player.getNickname());
        } else if (player.getAdditionalStatus() == PlayerStatusExtended.TUTORIAL_NICKNAME) {
            tutorial.tutorialSetNickname(player);
            return;
        } else {
            playerService.update(player);
            message = translation.get(player.getLanguage()).nicknameChanged(player.getNickname());
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
                messages.sendMessage("Очков опыта получено: " + experience, player.getExternalId());
            }
            return player;
        } catch (SOCInvalidDataException e) {
            messages.sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
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
                messages.sendMessage(translation.get(message.getPlayer().getLanguage()).statInvalid(), message.getUserId());
            } else {
                Player player = message.getPlayer();
                String result = player.raiseStat(stat, Integer.valueOf(commandParts[2]), player.getLanguage(), translation);
                if (!result.equals(translation.get(message.getPlayer().getLanguage()).statInvalid() )) playerService.update(player);
                messages.sendCallbackAnswer(message.getQueryId(), result);
                if (player.getUnassignedPoints() == 0) {
                    messages.sendMessageEdit(message.getMessageId(), player.getStatMenu(translation), player.getExternalId(), false);
                    tutorial.tutorialStatsRaised(message.getPlayer());
                } else {
                    messages.sendMessageEdit(message.getMessageId(), player.getStatMenu(translation), KeyboardManager.getKeyboardForStatUp(player.getUnassignedPoints()), player.getExternalId(), false);
                }
            }
        } else {
            messages.sendMessage(translation.get(message.getPlayer().getLanguage()).commandInvalid(), message.getUserId());
        }
    }

    public void fight(UpdateWrapper message) {
        messages.sendMessage("Да будет бой!", message.getUserId());
    }

}
