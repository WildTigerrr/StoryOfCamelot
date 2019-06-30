package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.*;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
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
    @Autowired
    private TranslationManager translation;

    public Boolean proceedTutorial(UpdateWrapper message) {
        Command command = ResponseHandler.messageToCommand(message.getText(), message.getPlayer().getLanguage());
        switch (message.getPlayer().getAdditionalStatus()) {
            case LANGUAGE_CHOOSE:
                if (!message.isQuery() && (command == Command.START || message.getPlayer().isNew())) {
                    Language lang = Language.ENG;
                    String langCode = message.getLanguage().substring(0, Math.min(message.getLanguage().length(), 2));
                    // List - https://datahub.io/core/language-codes/r/3.html
                    if (langCode.equals("ru")) lang = Language.RUS;
                    else if (langCode.equals("uk")) lang = Language.UKR;
                    gameMain.sendLanguageSelector(message.getUserId(), lang);
                } else if (message.isQuery() && command == Command.LANG) {
                    String[] commandParts = message.getText().split(" ", 2);
                    if (StringUtils.isNumeric(commandParts[1]) && Integer.valueOf(commandParts[1]) < Language.values().length) {
                        Player player = message.getPlayer();
                        player.setLanguage(Language.values()[Integer.valueOf(commandParts[1])]);
                        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_NICKNAME);
                        messages.sendMessageEdit(
                                message.getMessageId(),
                                translation.get(player.getLanguage()).languageSelected(), //MainText.LANGUAGE_SELECTED.text(player.getLanguage()),
                                message.getUserId(),
                                true
                        );
                        tutorialStart(player);
                    } else {
                        messages.sendMessage(translation.get(message.getPlayer().getLanguage()).commandInvalid(), message.getUserId()); // MainText.COMMAND_INVALID.text(message.getPlayer().getLanguage())
                    }
                } else {
                    messages.sendMessage(translation.get(message.getPlayer().getLanguage()).tutorialNoRush(), message.getUserId()); // MainText.TUTORIAL_NO_RUSH.text(message.getPlayer().getLanguage())
                }
                break;
            case TUTORIAL_NICKNAME:
                tutorialNickname(message.getPlayer(), message.getText());
                break;
            case TUTORIAL_MOVEMENT:
                if (command == Command.MOVE) {
                    return false;
                } else {
                    noRush(message.getPlayer().getLanguage(), message.getUserId());
                    // messages.sendMessage(translation.get(message.getPlayer().getLanguage()).tutorialNoRush(), message.getUserId()); // MainText.TUTORIAL_NO_RUSH.text(message.getPlayer().getLanguage())
                }
                break;
            case TUTORIAL_STATS:
                if (command == Command.ME) {
                    messages.sendMessage(message.getPlayer().toString(), message.getUserId(), true);
                    tutorialStats(message.getPlayer());
                } else {
                    noRush(message.getPlayer().getLanguage(), message.getUserId()); // MainText.TUTORIAL_NO_RUSH.text(message.getPlayer().getLanguage())
                }
                break;
            case TUTORIAL_STATS_UP:
                if (command == Command.SKILLS) {
                    tutorialStatsUp(message.getPlayer());
                } else {
                    noRush(message.getPlayer().getLanguage(), message.getUserId()); // MainText.TUTORIAL_NO_RUSH.text(message.getPlayer().getLanguage())
                }
                break;
            case TUTORIAL_STATS_UP_2:
                if (command == Command.UP) {
                    gameMain.statUp(message);
                } else {
                    noRush(message.getPlayer().getLanguage(), message.getUserId()); // MainText.TUTORIAL_NO_RUSH.text(message.getPlayer().getLanguage())
                }
                break;
            default:
                messages.sendMessage(translation.get(message.getPlayer().getLanguage()).commandNotDeveloped(), message.getUserId()); // MainText.COMMAND_NOT_DEVELOPED.text(message.getPlayer().getLanguage())
        }
        return true;
    }

    private void noRush(Language lang, String userId) {
        messages.sendMessage(translation.get(lang).tutorialNoRush(), userId);
    }

    public void tutorialStart(Player player) {
        System.out.println("New player");
        player.setup();
        playerService.update(player);
        messages.sendMessage(
                //MainText.MEET_NEW_PLAYER.text(player.getLanguage()),
                translation.get(player.getLanguage()).tutorialMeetNewPlayer(),
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
                //MainText.NICKNAME_SET.text(
                //        player.getLanguage(),
                //        player.getNickname(),
                //        locationService.findByName(GameSettings.FIRST_FOREST_LOCATION.get()).getName(player.getLanguage())
                //),
                translation.get(player.getLanguage()).tutorialNicknameSet(
                        player.getNickname(),
                        locationService.findByName(GameSettings.FIRST_FOREST_LOCATION.get()).getName(player.getLanguage())
                ),
                KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.MOVE)), player.getLanguage()),
                player.getExternalId()
        );
    }

    void tutorialMovement(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS);
        playerService.update(player);
        ArrayList<ReplyButton> buttons = new ArrayList<>();
        buttons.add(ReplyButton.ME);
        buttons.add(ReplyButton.MOVE);
        messages.sendMessage(
                //MainText.GUARD_LESSON_ONE.text(
                //        player.getLanguage(),
                //        player.getNickname()
                //),
                translation.get(player.getLanguage()).tutorialGuardLessonOne(player.getNickname()),
                KeyboardManager.getReplyByButtons(buttons, player.getLanguage()),
                player.getExternalId()
        );
    }

    private void tutorialStats(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS_UP);
        playerService.update(player);
        messages.sendMessage(
                //MainText.GUARD_LESSON_TWO.text(player.getLanguage()),
                translation.get(player.getLanguage()).tutorialGuardLessonTwo(),
                KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.SKILLS)), player.getLanguage()),
                player.getExternalId()
        );
    }

    private void tutorialStatsUp(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS_UP_2);
        playerService.update(player);
        gameMain.sendSkillWindow(player);
    }

    void tutorialStatsRaised(Player player) {
        // TODO Tutorial
        messages.sendMessage(
                //MainText.GUARD_LESSON_THREE.text(player.getLanguage()),
                translation.get(player.getLanguage()).tutorialGuardLessonThree(),
                player.getExternalId()
        );
    }



}
