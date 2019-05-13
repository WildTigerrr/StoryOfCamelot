package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.GameSettings;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButtons;
import com.wildtigerrr.StoryOfCamelot.bin.exceptions.SOCInvalidDataException;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

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
            message = MainText.NICKNAME_WRONG.text();
        } else if (!player.setNickname(newName)) {
            message = MainText.NICKNAME_LONG.text(String.valueOf(Player.getNicknameLengthMax()));
        } else if (player.getNickname().isEmpty()) {
            message = MainText.NICKNAME_EMPTY.text();
        } else if (playerService.findByNickname(player.getNickname()) != null) {
            message = MainText.NICKNAME_DUPLICATE.text(player.getNickname());
        } else if (player.getAdditionalStatus() == PlayerStatusExtended.TUTORIAL_NICKNAME) {
            tutorial.tutorialSetNickname(player);
        } else {
            playerService.update(player);
            message = MainText.NICKNAME_CHANGED.text(player.getNickname());
        }
        messages.sendMessage(message, player.getExternalId(), true);
    }

    public Player addExperience(Player player, Stats stat, int experience, Boolean sendExperienceGet) {
        try {
            ArrayList<String> eventList = player.addStatExp(
                    experience,
                    stat
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

}
