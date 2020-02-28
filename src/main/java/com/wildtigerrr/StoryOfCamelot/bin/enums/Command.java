package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMovement;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.BattleService;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.player.ExperienceService;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.database.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Log4j2
public enum Command {
    ME {
        @Override
        public boolean execute(UpdateWrapper update) {
            log.warn(update.getPlayer().toString());
            playerService.sendPlayerInfo(update);
            return true;
        }
    },
    NICKNAME {
        @Override
        public boolean execute(UpdateWrapper update) {
            playerService.setNickname(update.getPlayer(), StringUtils.emptyIfOutOfBounds(update.getText().split(" ", 2), 1));
            return true;
        }
    },
    ADD {
        @Override
        public boolean execute(UpdateWrapper update) {
            experienceService.addStatPoints(update);
            return true;
        }
    },
    ACTION,
    SEND {
        @Override
        public boolean execute(UpdateWrapper update) {
            game.sendMessageToUser(update.getMessage());
            return true;
        }
    },
    MOVE {
        @Override
        public boolean execute(UpdateWrapper update) {
            gameMovement.handleMove(update);
            return true;
        }
    },
    SKILLS {
        @Override
        public boolean execute(UpdateWrapper update) {
            experienceService.sendSkillWindow(update.getPlayer());
            return true;
        }
    },
    START,
    UP {
        @Override
        public boolean execute(UpdateWrapper update) {
            experienceService.statUp(update);
            return true;
        }
    },
    LANG,
    TOP {
        @Override
        public boolean execute(UpdateWrapper update) {
            game.sendTopPlayers(update.getUserId());
            return true;
        }
    },
    FIGHT {
        @Override
        public boolean execute(UpdateWrapper update) {
            battleService.fight(update);
            return true;
        }
    };

    private static GameMain game;
    private static GameMovement gameMovement;
    private static BattleService battleService;
    private static PlayerService playerService;
    private static ExperienceService experienceService;

    public boolean execute(UpdateWrapper update) {
        game.sendDumb(update);
        return false;
    }

    @Component
    public static class DependencyInjector {
        private final GameMain game;
        private final GameMovement gameMovement;
        private final BattleService battleService;
        private final PlayerService playerService;
        private final ExperienceService experienceService;

        @Autowired
        public DependencyInjector(
                GameMain game,
                GameMovement gameMovement,
                BattleService battleService,
                PlayerService playerService,
                ExperienceService experienceService
        ) {
            this.game = game;
            this.gameMovement = gameMovement;
            this.battleService = battleService;
            this.playerService = playerService;
            this.experienceService = experienceService;
        }

        @PostConstruct
        public void postConstruct() {
            Command.setDependencies(game, gameMovement, battleService, playerService, experienceService);
        }
    }

    private static void setDependencies(
            GameMain gameDep,
            GameMovement gameMovementDep,
            BattleService battleServiceDep,
            PlayerService playerServiceDep,
            ExperienceService experienceServiceDep
    ) {
        game = gameDep;
        gameMovement = gameMovementDep;
        battleService = battleServiceDep;
        playerService = playerServiceDep;
        experienceService = experienceServiceDep;
    }

}
