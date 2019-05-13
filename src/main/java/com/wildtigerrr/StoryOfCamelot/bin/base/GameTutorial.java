package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.GameSettings;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButtons;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import com.wildtigerrr.StoryOfCamelot.web.UpdateWrapper;
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
    private GameMain gameMain;
    @Autowired
    private PlayerServiceImpl playerService;
    @Autowired
    private LocationServiceImpl locationService;

    public Boolean proceedTutorial(UpdateWrapper message) {
        Command command = ResponseHandler.messageToCommand(message.getText());
        switch (message.getPlayer().getAdditionalStatus()) {
            case TUTORIAL_NICKNAME:
                if (command == Command.START) {
                    tutorialStart(message.getPlayer());
                } else {
                    tutorialNickname(message.getPlayer(), message.getText());
                }
                break;
            case TUTORIAL_MOVEMENT:
                if (command == Command.MOVE) {
                    return false;
                } else {
                    messages.sendMessage(MainText.TUTORIAL_NO_RUSH.text(), message.getUserId());
                }
                break;
            case TUTORIAL_STATS:
                if (command == Command.ME) {
                    messages.sendMessage(playerService.getPlayerInfo(message.getUserId()), message.getUserId(), true);
                    tutorialStats(message.getPlayer());
                } else {
                    messages.sendMessage(MainText.TUTORIAL_NO_RUSH.text(), message.getUserId());
                }
                break;
            case TUTORIAL_STATS_UP:
                if (command == Command.SKILLS) {
                    messages.sendMessage(MainText.TUTORIAL_STUCK.text(), message.getUserId());
                } else {
                    messages.sendMessage(MainText.TUTORIAL_NO_RUSH.text(), message.getUserId());
                }
                break;
            default:
                messages.sendMessage(MainText.COMMAND_NOT_DEVELOPED.text(), message.getUserId());
        }
        return true;
    }

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

    private void tutorialNickname(Player player, String nickname) {
        gameMain.setNickname(player, nickname);
    }

    void tutorialSetNickname(Player player) {
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

    public void tutorialStats(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS_UP);
        playerService.update(player);
        messages.sendMessage(
                MainText.GUARD_LESSON_TWO.text(),
                KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButtons.SKILLS))),
                player.getExternalId()
        );
    }

}
