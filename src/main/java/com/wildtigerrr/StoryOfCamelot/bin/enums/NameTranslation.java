package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum NameTranslation {
    FOREST(
            Emoji.EVERGREEN_TREE.getCode() + " Forest",
            Emoji.EVERGREEN_TREE.getCode() + " Лес",
            Emoji.EVERGREEN_TREE.getCode() + " Ліс"
    ),
    THICKET(
            "",
            Emoji.EVERGREEN_TREE.getCode() + " Дебри",
            ""
    ),
    CAVE(
            "",
            Emoji.BAT.getCode() + " Таинственная Пещера",
            ""
    ),
    TRADING_SQUARE(
            "",
            Emoji.CIRCUS_TENT.getCode() + " Торговая Площадь",
            ""
    );
    private final String nameENG;
    private final String nameRUS;
    private final String nameUKR;

    NameTranslation(String nameENG, String nameRUS, String nameUKR) {
        this.nameENG = nameENG;
        this.nameRUS = nameRUS;
        this.nameUKR = nameUKR;
    }

    public String getName(Language lang) {
        switch (lang) {
            case ENG:
                return nameENG;
            case RUS:
                return nameRUS;
            case UKR:
                return nameUKR;
            default:
                return "No such language";
        }
    }

}
