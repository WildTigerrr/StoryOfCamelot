package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum NameTranslation {
    FOREST(
            Emoji.EVERGREEN_TREE.getCode() + " Forest",
            Emoji.EVERGREEN_TREE.getCode() + " Лес",
            Emoji.EVERGREEN_TREE.getCode() + " Ліс"
    ),
    THICKET(
            Emoji.EVERGREEN_TREE.getCode() + " Thicket",
            Emoji.EVERGREEN_TREE.getCode() + " Дебри",
            Emoji.EVERGREEN_TREE.getCode() + " Нетрі"
    ),
    CAVE(
            Emoji.BAT.getCode() + " Mythical Cave",
            Emoji.BAT.getCode() + " Таинственная Пещера",
            Emoji.BAT.getCode() + " Загадкова Печера"
    ),
    TRADING_SQUARE(
            Emoji.CIRCUS_TENT.getCode() + " Merchant Square",
            Emoji.CIRCUS_TENT.getCode() + " Торговая Площадь",
            Emoji.CIRCUS_TENT.getCode() + " Торгівельна Площа"
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
