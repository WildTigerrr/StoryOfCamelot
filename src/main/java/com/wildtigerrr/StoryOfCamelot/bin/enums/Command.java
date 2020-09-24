package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.BattleService;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.player.ExperienceService;
import com.wildtigerrr.StoryOfCamelot.bin.handler.CommandHandler;
import com.wildtigerrr.StoryOfCamelot.bin.service.ApplicationContextProvider;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Log4j2
public enum Command {
    ME("playerCommandHandler"),
    NICKNAME ("nicknameCommandHandler"),
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
    MOVE("moveCommandHandler"),
    SKILLS("skillsCommandHandler"),
    START("startCommandHandler"),
    UP ("skillsCommandHandler"),
    LANG("languageCommandHandler"),
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
    },
    SEARCH_ENEMIES("fightCommandHandler"),
    NOTIFY("notifyCommandHandler"),
    BAN("adminCommandHandler"),
    DEFAULT;

    private final String handlerName;

    private static GameMain game;
    private static BattleService battleService;
    private static ExperienceService experienceService;

    Command(String handlerName) {
        this.handlerName = handlerName;
    }

    Command() {
        this.handlerName = "defaultCommandHandler";
    }

    public CommandHandler handler() {
        return ApplicationContextProvider.bean(handlerName);
    }

    public boolean execute(UpdateWrapper update) {
        game.sendDumb(update);
        return false;
    }

    @Component
    public static class DependencyInjector {
        private final GameMain game;
        private final BattleService battleService;
        private final ExperienceService experienceService;

        @Autowired
        public DependencyInjector(
                GameMain game,
                BattleService battleService,
                ExperienceService experienceService
        ) {
            this.game = game;
            this.battleService = battleService;
            this.experienceService = experienceService;
        }

        @PostConstruct
        public void postConstruct() {
            Command.setDependencies(game, battleService, experienceService);
        }
    }

    private static void setDependencies(
            GameMain gameDep,
            BattleService battleServiceDep,
            ExperienceService experienceServiceDep
    ) {
        game = gameDep;
        battleService = battleServiceDep;
        experienceService = experienceServiceDep;
    }

}
