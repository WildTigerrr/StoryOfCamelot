package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.base.GameMain;
import com.wildtigerrr.StoryOfCamelot.bin.base.GameMovement;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

public enum Command {
    ME {
        @Override
        public boolean execute(UpdateWrapper update) {
            game.sendPlayerInfo(update);
            return true;
        }
    },
    NICKNAME {
        @Override
        public boolean execute(UpdateWrapper update) {
            game.setNickname(update.getPlayer(), StringUtils.emptyIfOutOfBounds(update.getText().split(" ", 2), 1));
            return true;
        }
    },
    ADD {
        @Override
        public boolean execute(UpdateWrapper update) {
            game.addStatPoints(update);
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
            game.sendSkillWindow(update.getPlayer());
            return true;
        }
    },
    START,
    UP {
        @Override
        public boolean execute(UpdateWrapper update) {
            game.statUp(update);
            return true;
        }
    },
    LANG,
    TOP {
        @Override
        public boolean execute(UpdateWrapper update) {
            game.getTopPlayers(update.getUserId());
            return true;
        }
    },
    FIGHT {
        @Override
        public boolean execute(UpdateWrapper update) {
            game.fight(update);
            return true;
        }
    };

    private static GameMain game;
    private static GameMovement gameMovement;

    public boolean execute(UpdateWrapper update) {
        game.sendDumb(update);
        return false;
    }

    @Component
    public static class DependencyInjector {
        private final GameMain game;
        private final GameMovement gameMovement;

        @Autowired
        public DependencyInjector(GameMain game, GameMovement gameMovement) {
            this.game = game;
            this.gameMovement = gameMovement;
        }

        @PostConstruct
        public void postConstruct() {
            Command.setDependencies(game, gameMovement);
        }
    }

    private static void setDependencies(GameMain gameMain, GameMovement gameMove) {
        game = gameMain;
        gameMovement = gameMove;
    }

}
