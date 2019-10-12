package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.*;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
@Service
public class GameTutorial {

    private final ResponseManager messages;
    private final GameMain gameMain;
    private final PlayerServiceImpl playerService;
    private final LocationServiceImpl locationService;
    private final TranslationManager translation;

    @Autowired
    public GameTutorial(
            ResponseManager messages,
            GameMain gameMain,
            PlayerServiceImpl playerService,
            LocationServiceImpl locationService,
            TranslationManager translation
    ) {
        this.messages = messages;
        this.gameMain = gameMain;
        this.playerService = playerService;
        this.locationService = locationService;
        this.translation = translation;
    }

    public Boolean proceedTutorial(UpdateWrapper message) {
        Command command = message.getCommand();
        switch (message.getPlayer().getAdditionalStatus()) {
            case LANGUAGE_CHOOSE:
                tutorialLanguageSelect(command, message);
                break;
            case TUTORIAL_NICKNAME:
                tutorialNickname(message.getPlayer(), message.getText());
                break;
            case TUTORIAL_MOVEMENT:
                if (command == Command.MOVE) {
                    return false;
                } else {
                    noRush(message.getPlayer().getLanguage(), message.getUserId());
                }
                break;
            case TUTORIAL_STATS:
                if (command == Command.ME) {
                    messages.sendMessage(message.getPlayer().toString(), message.getUserId(), true);
                    tutorialStats(message.getPlayer());
                } else {
                    noRush(message.getPlayer().getLanguage(), message.getUserId());
                }
                break;
            case TUTORIAL_STATS_UP:
                if (command == Command.SKILLS) {
                    tutorialInitiateStatsUp(message.getPlayer());
                } else {
                    noRush(message.getPlayer().getLanguage(), message.getUserId());
                }
                break;
            case TUTORIAL_STATS_UP_2:
                if (command == Command.UP) {
                    tutorialProceedStatsUpProgress(message);
                } else {
                    noRush(message.getPlayer().getLanguage(), message.getUserId());
                }
                break;
            case TUTORIAL_FIGHT:
                if (command == Command.FIGHT) {
                    gameMain.fight(message);
                } else {
                    noRush(message.getPlayer().getLanguage(), message.getUserId());
                }
                break;
            default:
                messages.sendMessage(translation.get(message.getPlayer().getLanguage()).commandNotDeveloped(), message.getUserId());
        }
        return true;
    }

    private void noRush(Language lang, String userId) {
        messages.sendMessage(translation.get(lang).tutorialNoRush(), userId);
    }

    private void tutorialLanguageSelect(Command command, UpdateWrapper message) {
        if (!message.isQuery() && (command == Command.START || message.getPlayer().isNew())) {
            Language lang = Language.ENG;
            String langCode = message.getLanguage().substring(0, Math.min(message.getLanguage().length(), 2));
            // List - https://datahub.io/core/language-codes/r/3.html
            if (langCode.equals("ru")) lang = Language.RUS;
            else if (langCode.equals("uk")) lang = Language.UKR;
            gameMain.sendLanguageSelector(message.getUserId(), lang);
        } else if (message.isQuery() && command == Command.LANG) {
            String[] commandParts = message.getText().split(" ", 2);
            if (StringUtils.isNumeric(commandParts[1]) && (Integer.parseInt(commandParts[1]) < Language.values().length)) {
                Player player = message.getPlayer();
                player.setLanguage(Language.values()[Integer.parseInt(commandParts[1])]);
                player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_NICKNAME); //translation.get(player.getLanguage()).languageSelected(),
                messages.sendMessageEdit(
                        message.getMessageId(),
                        translation.getMessage("tutorial.lang.selected", player.getLanguage(), new Object[]{player.getLanguage().getName()}),
                        message.getUserId(),
                        true
                );
                tutorialStart(player);
            } else {
                messages.sendMessage(translation.get(message.getPlayer().getLanguage()).commandInvalid(), message.getUserId());
            }
        } else {
            messages.sendMessage(translation.get(message.getPlayer().getLanguage()).tutorialNoRush(), message.getUserId());
        }
    }

    public void tutorialStart(Player player) {
        log.info("New Player: " + player);
        player.setup();
        playerService.update(player); // translation.get(player.getLanguage()).tutorialMeetNewPlayer(),
        messages.sendMessage(
                translation.getMessage("tutorial.player.welcome", player.getLanguage()),
                player.getExternalId(),
                true
        );
    }

    private void tutorialNickname(Player player, String nickname) {
        gameMain.setNickname(player, nickname);
        tutorialSetNickname(player);
    }

    void tutorialSetNickname(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_MOVEMENT);
        playerService.update(player);
        messages.sendMessage(
                translation.getMessage(
                        "tutorial.player.nickname.accept",
                        player.getLanguage(),
                        new Object[]{
                                player.getNickname(),
                                locationService.findByName(GameSettings.FIRST_FOREST_LOCATION.get()).getName(player.getLanguage())
                        }),
                KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.MOVE)), player.getLanguage()),
                player.getExternalId()
        );
    }

    void tutorialMovement(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS);
        playerService.update(player);
        List<ReplyButton> buttons = new ArrayList<ReplyButton>() {
            {
                add(ReplyButton.ME);
                add(ReplyButton.MOVE);
            }
        };
        messages.sendMessage(
                translation.get(player.getLanguage()).tutorialGuardLessonOne(player.getNickname()),
                KeyboardManager.getReplyByButtons(buttons, player.getLanguage()),
                player.getExternalId()
        );
    }

    private void tutorialStats(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS_UP);
        playerService.update(player);
        messages.sendMessage(
                translation.get(player.getLanguage()).tutorialGuardLessonTwo(),
                KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.SKILLS)), player.getLanguage()),
                player.getExternalId()
        );
    }

    private void tutorialInitiateStatsUp(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS_UP_2);
        playerService.update(player);
        gameMain.sendSkillWindow(player);
    }

    private void tutorialProceedStatsUpProgress(UpdateWrapper message) {
        gameMain.statUp(message);
        if (message.getPlayer().getUnassignedPoints() == 0)
            tutorialStatsRaised(message.getPlayer());
    }

    private void tutorialStatsRaised(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_FIGHT);
        playerService.update(player);
        messages.sendMessage(
                translation.get(player.getLanguage()).tutorialGuardLessonThree(),
                KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.FIGHT)), player.getLanguage()),
                player.getExternalId()
        );
    }


}
