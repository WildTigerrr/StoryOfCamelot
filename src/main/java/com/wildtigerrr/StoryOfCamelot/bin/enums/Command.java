package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

public enum Command {
    ME{
        @Override
        public void execute(UpdateWrapper update) {
            System.out.println("Executing ME command:");
            game.sendPlayerInfo(update);
        }
    },
    NICKNAME,
    ADD,
    ACTION,
    SEND,
    MOVE,
    SKILLS,
    START,
    UP,
    LANG,
    TOP,
    FIGHT;

    private static GameMain game;

    public void execute(UpdateWrapper update) {

    }

    @Component
    public static class DependencyInjector {
        private final GameMain game;

        @Autowired
        public DependencyInjector(GameMain game) {
            this.game = game;
        }

        @PostConstruct
        public void postConstruct() {
            Command.setDependencies(game);
        }
    }

    private static void setDependencies(GameMain gameMain) {
        game = gameMain;
    }

}
