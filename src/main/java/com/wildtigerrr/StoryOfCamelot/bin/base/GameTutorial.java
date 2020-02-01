package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.*;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.EditResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
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
                    messages.sendMessage(TextResponseMessage.builder()
                            .text(message.getPlayer().toString()).targetId(message).applyMarkup(true).build()
                    );
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
                messages.sendMessage(TextResponseMessage.builder()
                        .text(translation.getMessage("commands.not-developed", message)).targetId(message).build()
                );
        }
        return true;
    }

    private void noRush(Language lang, String userId) {
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("tutorial.lessons.no-rush", lang)).targetId(userId).build()
        );
    }

    private void tutorialLanguageSelect(Command command, UpdateWrapper message) {
        if (!message.isQuery() && (command == Command.START || message.getPlayer().isNew())) {
            setStartingLanguage(message);
        } else if (message.isQuery() && command == Command.LANG) {
            String[] commandParts = message.getText().split(" ", 2);
            if (Language.isValidLanguageCode(commandParts[1])) {
                Player player = message.getPlayer();
                player.setLanguage(Language.values()[Integer.parseInt(commandParts[1])]);
                player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_NICKNAME);
                messages.sendMessage(EditResponseMessage.builder()
                        .messageId(message)
                        .text(translation.getMessage("tutorial.lang.selected",
                                player,
                                new Object[]{player.getLanguage().getName()}
                        ))
                        .targetId(message)
                        .applyMarkup(true).build()
                );
                tutorialStart(player);
            } else {
                messages.sendMessage(TextResponseMessage.builder()
                        .text(translation.getMessage("commands.invalid", message)).targetId(message).build()
                );
            }
        } else {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(translation.getMessage("tutorial.lessons.no-rush", message)).targetId(message).build()
            );
        }
    }

    public void setStartingLanguage(UpdateWrapper update) {
        String langCode = update.getUserLanguageCode().substring(0, Math.min(update.getUserLanguageCode().length(), 2));
        Language lang = Language.byCountryCode(langCode);
        gameMain.sendLanguageSelector(update.getUserId(), lang);
    }

    public void tutorialStart(Player player) {
        log.info("New Player: " + player);
        player.setup();
        playerService.update(player);
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("tutorial.player.welcome", player)).targetId(player).applyMarkup(true).build()
        );
    }

    private void tutorialNickname(Player player, String nickname) {
        gameMain.setNickname(player, nickname);
        tutorialSetNickname(player);
    }

    void tutorialSetNickname(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_MOVEMENT);
        playerService.update(player);
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage(
                        "tutorial.player.nickname.accept",
                        player,
                        new Object[]{
                                player.getNickname(),
                                locationService.findByName(GameSettings.FIRST_FOREST_LOCATION.get()).getName(player.getLanguage())
                        }))
                .keyboard(KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.MOVE)), player.getLanguage()))
                .targetId(player)
                .applyMarkup(true).build()
        );
    }

    void tutorialMovement(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS);
        playerService.update(player);
        List<ReplyButton> buttons = new ArrayList<>() {
            {
                add(ReplyButton.ME);
                add(ReplyButton.MOVE);
            }
        };
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage(
                        "tutorial.lessons.first-profile", player, new Object[]{player.getNickname(), ReplyButton.ME.getLabel(player.getLanguage())}
                ))
                .keyboard(KeyboardManager.getReplyByButtons(buttons, player.getLanguage()))
                .targetId(player)
                .applyMarkup(true).build()
        );
    }

    private void tutorialStats(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS_UP);
        playerService.update(player);
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("tutorial.lessons.two-skillpoints", player))
                .keyboard(KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.SKILLS)), player.getLanguage()))
                .targetId(player).build()
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
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("tutorial.lessons.three-fight", player, new Object[]{NameTranslation.MOB_FLYING_SWORD.getName(player)}))
                .keyboard(KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.FIGHT)), player.getLanguage()))
                .targetId(player).build()
        );
    }

}
