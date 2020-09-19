package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum ReplyButton {
    MOVE(NameTranslation.BUTTON_MOVE, Command.MOVE),
    SKILLS(NameTranslation.BUTTON_SKILLS, Command.SKILLS),
    ME(NameTranslation.BUTTON_ME, Command.ME),
    FIGHT(NameTranslation.BUTTON_FIGHT, Command.FIGHT),
    SEARCH_ENEMIES(NameTranslation.BUTTON_SEARCH_ENEMIES, Command.FIGHT);

    private final NameTranslation label;
    @Getter
    private final Command command;

    ReplyButton(NameTranslation label, Command command) {
        this.label = label;
        this.command = command;
    }

    public String getLabel(Language lang) {
        return label.getName(lang);
    }

    public String getLabel(Player player) {
        return getLabel(player.getLanguage());
    }

    public static Command buttonToCommand(String text, Language lang) {
        log.debug("Incoming command identification: " + text);
        for (ReplyButton button : ReplyButton.values()) {
            log.debug(button.getLabel(lang));
            if (text.equals(button.getLabel(lang))) return button.getCommand();
        }
        log.debug("Returning default");
        return Command.DEFAULT;
    }
}