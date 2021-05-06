package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.handler.CommandHandler;
import com.wildtigerrr.StoryOfCamelot.bin.service.ApplicationContextProvider;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum Command {
    START("startCommandHandler"),
    BACK("startCommandHandler"),
    IGNORE("startCommandHandler"),
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
    STORE("storeCommandHandler"),
    STORES("storeCommandHandler"),
    STORE_SELECT("storeCommandHandler"),
    NOTIFY("notifyCommandHandler"),
    ID("adminCommandHandler"),
    BAN("adminCommandHandler"),
    PING("adminCommandHandler"),
    SEND("sendCommandHandler"),
    TEST("adminCommandHandler"),
    DEFAULT;

    private final String handlerName;

    Command(String handlerName) {
        this.handlerName = handlerName;
    }

    Command() {
        this.handlerName = "defaultCommandHandler";
    }

    public CommandHandler handler() {
        return ApplicationContextProvider.bean(handlerName);
    }

}
