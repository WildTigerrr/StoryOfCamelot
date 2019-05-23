package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.GameSettings;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.exceptions.SOCInvalidDataException;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

    public void sendLanguageSelector(String userId, Language lang) {
        messages.sendMessage(
                MainText.LANGUAGE_SELECT.text(lang),
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
            message = MainText.NICKNAME_WRONG.text(player.getLanguage());
        } else if (!player.setNickname(newName)) {
            message = MainText.NICKNAME_LONG.text(player.getLanguage(),  String.valueOf(Player.getNicknameLengthMax()));
        } else if (player.getNickname().isEmpty()) {
            message = MainText.NICKNAME_EMPTY.text(player.getLanguage());
        } else if (playerService.findByNickname(player.getNickname()) != null) {
            message = MainText.NICKNAME_DUPLICATE.text(player.getLanguage(), player.getNickname());
        } else if (player.getAdditionalStatus() == PlayerStatusExtended.TUTORIAL_NICKNAME) {
            tutorial.tutorialSetNickname(player);
            return;
        } else {
            playerService.update(player);
            message = MainText.NICKNAME_CHANGED.text(player.getLanguage(), player.getNickname());
        }
        messages.sendMessage(message, player.getExternalId(), true);
    }

    public Player addExperience(Player player, Stats stat, int experience, Boolean sendExperienceGet) {
        try {
            ArrayList<String> eventList = player.addStatExp(
                    experience,
                    stat,
                    player.getLanguage()
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
        messages.sendMessage(player.getStatMenu(), KeyboardManager.getKeyboardForStatUp(player.getUnassignedPoints()), player.getExternalId());
    }

    public void statUp(UpdateWrapper message) {
        String[] commandParts = message.getText().split("_", 3);
        if (commandParts.length == 3 && commandParts[1].length() == 1 && StringUtils.isNumeric(commandParts[2])) {
            Stats stat = Stats.getStat(commandParts[1]);
            if (stat == null) {
                messages.sendMessage(MainText.STAT_INVALID.text(message.getPlayer().getLanguage()), message.getUserId());
            } else {
                Player player = message.getPlayer();
                String result = player.raiseStat(stat, Integer.valueOf(commandParts[2]), player.getLanguage());
                if (!result.equals(MainText.STAT_INVALID.text(message.getPlayer().getLanguage()))) playerService.update(player);
                messages.sendCallbackAnswer("" + message.getMessageId(), result);
                if (player.getUnassignedPoints() == 0) {
                    messages.sendMessageEdit(message.getMessageId(), player.getStatMenu(), player.getExternalId(), false);
                    tutorial.tutorialStatsRaised(message.getPlayer());
                } else {
                    messages.sendMessageEdit(message.getMessageId(), player.getStatMenu(), KeyboardManager.getKeyboardForStatUp(player.getUnassignedPoints()), player.getExternalId(), false);
                }
            }
        } else {
            messages.sendMessage(MainText.COMMAND_INVALID.text(message.getPlayer().getLanguage()), message.getUserId());
        }
    }

}
