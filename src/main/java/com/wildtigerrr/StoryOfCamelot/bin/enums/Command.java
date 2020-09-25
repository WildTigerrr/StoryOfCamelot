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
    START("startCommandHandler"),
    BACK("startCommandHandler"),
    ME("playerCommandHandler"),
    PLAYERS_TOP("playerCommandHandler"),
    NICKNAME ("nicknameCommandHandler"),
    LANG("languageCommandHandler"),
    MOVE("moveCommandHandler"),
    SKILLS("skillsCommandHandler"),
    UP ("skillsCommandHandler"),
    FIGHT("fightCommandHandler"),
    SEARCH_ENEMIES("fightCommandHandler"),
    BACKPACK("backpackCommandHandler"),
    NOTIFY("notifyCommandHandler"),
    BAN("adminCommandHandler"),
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
    TEST("adminCommandHandler"),
    DEFAULT;

    private final String handlerName;

    private static GameMain game;
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
        private final ExperienceService experienceService;

        @Autowired
        public DependencyInjector(
                GameMain game,
                ExperienceService experienceService
        ) {
            this.game = game;
            this.experienceService = experienceService;
        }

        @PostConstruct
        public void postConstruct() {
            Command.setDependencies(game, experienceService);
        }
    }

    private static void setDependencies(
            GameMain gameDep,
            ExperienceService experienceServiceDep
    ) {
        game = gameDep;
        experienceService = experienceServiceDep;
    }

}
