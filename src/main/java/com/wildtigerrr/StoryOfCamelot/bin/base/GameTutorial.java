package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.LanguageService;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.player.ExperienceService;
import com.wildtigerrr.StoryOfCamelot.bin.enums.*;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.MobTemplate;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.MobService;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
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
    private final TranslationManager translation;
    private final PlayerServiceImpl playerService;
    private final LocationServiceImpl locationService;
    private final LanguageService languageService;
    private final ExperienceService experienceService;
    private final MobService mobService;
    private final CacheProvider cacheService;

    @Autowired
    public GameTutorial(
            ResponseManager messages,
            TranslationManager translation,
            PlayerServiceImpl playerService,
            LocationServiceImpl locationService,
            LanguageService languageService,
            ExperienceService experienceService,
            MobService mobService,
            CacheProvider cacheProvider
    ) {
        this.messages = messages;
        this.translation = translation;
        this.playerService = playerService;
        this.locationService = locationService;
        this.languageService = languageService;
        this.experienceService = experienceService;
        this.mobService = mobService;
        this.cacheService = cacheProvider;
    }

    public Boolean proceedTutorial(UpdateWrapper update) {
        Command command = update.getCommand();
        switch (update.getPlayer().getAdditionalStatus()) {
            case LANGUAGE_CHOOSE:
                tutorialLanguageSelect(command, update);
                break;
            case TUTORIAL_NICKNAME:
                tutorialNickname(update.getPlayer(), update.getText());
                break;
            case TUTORIAL_MOVEMENT:
                if (command == Command.MOVE) {
                    return false;
                } else {
                    noRush(update.getPlayer().getLanguage(), update.getUserId());
                }
                break;
            case TUTORIAL_STATS:
                if (command == Command.ME) {
                    command.execute(update);
                    tutorialStats(update.getPlayer());
                } else {
                    noRush(update.getPlayer().getLanguage(), update.getUserId());
                }
                break;
            case TUTORIAL_STATS_UP:
                if (command == Command.SKILLS) {
                    tutorialInitiateStatsUp(update.getPlayer());
                } else {
                    noRush(update.getPlayer().getLanguage(), update.getUserId());
                }
                break;
            case TUTORIAL_STATS_UP_2:
                if (command == Command.UP) {
                    tutorialProceedStatsUpProgress(update);
                } else {
                    noRush(update.getPlayer().getLanguage(), update.getUserId());
                }
                break;
            case TUTORIAL_FIGHT:
                if (command == Command.FIGHT) {
                    tutorialBattle(update);
                } else {
                    noRush(update.getPlayer().getLanguage(), update.getUserId());
                }
                break;
            default:
                messages.sendMessage(TextResponseMessage.builder()
                        .text(translation.getMessage("commands.not-developed", update)).targetId(update).build()
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
        languageService.sendLanguageSelector(update.getUserId(), lang);
    }

    public void tutorialStart(Player player) {
        log.info("New Player: " + player);
//        player.setup();
        playerService.update(player);
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("tutorial.player.welcome", player)).targetId(player).applyMarkup(true).build()
        );
    }

    private void tutorialNickname(Player player, String nickname) {
        playerService.setNickname(player, nickname);
        if (player.getNickname().equals(nickname)) {
            tutorialSetNickname(player);
        }
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
                        "tutorial.lessons.first-profile", player, new Object[]{player.getNickname(), ReplyButton.ME.getLabel(player)}
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
                .text(translation.getMessage("tutorial.lessons.two-skillpoints", player, new Object[]{ReplyButton.SKILLS.getLabel(player)}))
                .keyboard(KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.SKILLS)), player.getLanguage()))
                .targetId(player).build()
        );
    }

    private void tutorialInitiateStatsUp(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS_UP_2);
        playerService.update(player);
        experienceService.sendSkillWindow(player);
    }

    private void tutorialProceedStatsUpProgress(UpdateWrapper message) {
        experienceService.statUp(message);
        if (message.getPlayer().stats().getUnassignedPoints() == 0)
            tutorialStatsRaised(message.getPlayer());
    }

    private void tutorialStatsRaised(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_FIGHT);
        playerService.update(player);
        Mob enemy = mobService.findBySystemName(MobTemplate.FLYING_SWORD.name());
        cacheService.add(CacheType.PLAYER_STATE, player.getId(), new PlayerState(player, enemy));
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("tutorial.lessons.three-fight", player, new Object[]{enemy.getName().getName(player)}))
                .keyboard(KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.FIGHT)), player.getLanguage()))
                .targetId(player).build()
        );
    }

    private void tutorialBattle(UpdateWrapper update) {
        update.getCommand().execute(update);

        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, update.getPlayer().getId());
        Mob mob = mobService.findById(state.getLastBattle().getEnemyId());
        String messageTemplate = state.getLastBattle().isWin() ? "battle.enemy-defeated" : "battle.player-defeated";
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage(messageTemplate, update, new Object[]{mob.getName(update)}))
                .targetId(update).build()
        );
    }

}
