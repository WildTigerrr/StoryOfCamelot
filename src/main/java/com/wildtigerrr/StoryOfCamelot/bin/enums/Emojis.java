package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.vdurmont.emoji.EmojiParser;

public enum  Emojis {
    EVERGREEN_TREE(EmojiParser.parseToUnicode(":evergreen_tree:")),
    CIRCUS_TENT(EmojiParser.parseToUnicode(":circus_tent:")),
    BAT(EmojiParser.parseToUnicode("\uD83E\uDD87")),
    FOOTPRINTS(EmojiParser.parseToUnicode(":footprints:")),
    SCROLL(EmojiParser.parseToUnicode(":scroll:"));

    private final String code;

    Emojis(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
