package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.GameSettings;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButtons;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class GameTutorial {

    @Autowired
    private ResponseManager messages;
    @Autowired
    private PlayerServiceImpl playerService;
    @Autowired
    private LocationServiceImpl locationService;

    public void tutorialStart(Player player) {
        System.out.println("New player");
        player.setup();
        playerService.update(player);
        messages.sendMessage(
                MainText.MEET_NEW_PLAYER.text(),
                player.getExternalId(),
                true
        );
    }

    void tutorialNickname(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_MOVEMENT);
        playerService.update(player);
        messages.sendMessage(
                MainText.NICKNAME_SET.text(
                        player.getNickname(),
                        locationService.findByName(GameSettings.FIRST_FOREST_LOCATION.get()).getName()
                ),
                KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButtons.MOVE))),
                player.getExternalId()
        );
    }

    void tutorialMovement(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS);
        playerService.update(player);
        ArrayList<ReplyButtons> buttons = new ArrayList<>();
        buttons.add(ReplyButtons.ME);
        buttons.add(ReplyButtons.MOVE);
        messages.sendMessage(
                MainText.GUARD_LESSON_ONE.text(
                        player.getNickname(),
                        ReplyButtons.ME.getLabel()
                ),
                KeyboardManager.getReplyByButtons(buttons),
                player.getExternalId()
        );
    }

    /*void tutorialStats() {

    }*/

}
