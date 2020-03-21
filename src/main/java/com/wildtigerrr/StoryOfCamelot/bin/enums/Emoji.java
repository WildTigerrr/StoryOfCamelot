package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.vdurmont.emoji.EmojiParser;

public enum Emoji {
    EVERGREEN_TREE(EmojiParser.parseToUnicode(":evergreen_tree:")),
    CIRCUS_TENT(EmojiParser.parseToUnicode(":circus_tent:")),
    BAT(EmojiParser.parseToUnicode("\uD83E\uDD87")),
    LIGHTNING(EmojiParser.parseToUnicode(":zap:")),

    // SKILLS
    STRENGTH(EmojiParser.parseToUnicode(":muscle:")),
    HEALTH(EmojiParser.parseToUnicode(":heart:")),
    AGILITY(EmojiParser.parseToUnicode("\uD83E\uDD38")),
    CHARISMA(EmojiParser.parseToUnicode(":performing_arts:")),
    INTELLIGENCE(EmojiParser.parseToUnicode(":mortar_board:")),
    ENDURANCE(EmojiParser.parseToUnicode(":runner:")),
    LUCK(EmojiParser.parseToUnicode(":four_leaf_clover:")),

    // BUTTONS
    FOOTPRINTS(EmojiParser.parseToUnicode(":footprints:")),
    SKILLS(EmojiParser.parseToUnicode(":mortar_board:")),
    SCROLL(EmojiParser.parseToUnicode(":scroll:")),
    FIGHT(EmojiParser.parseToUnicode(":crossed_swords:")),

    // FLAGS
    FLAG_ENGLAND(EmojiParser.parseToUnicode("\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F")),
    FLAG_UKRAINE(EmojiParser.parseToUnicode("\uD83C\uDDFA\uD83C\uDDE6")),
    FLAG_RUSSIA(EmojiParser.parseToUnicode(":ru:"))
    ;

    private final String code;

    Emoji(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}