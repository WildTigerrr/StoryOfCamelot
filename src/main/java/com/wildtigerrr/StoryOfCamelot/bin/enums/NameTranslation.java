package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum NameTranslation {
    // LOCATIONS
    LOC_FOREST(
            Emoji.EVERGREEN_TREE.getCode() + " Forest",
            Emoji.EVERGREEN_TREE.getCode() + " Лес",
            Emoji.EVERGREEN_TREE.getCode() + " Ліс"
    ),
    LOC_THICKET(
            Emoji.EVERGREEN_TREE.getCode() + " Thicket",
            Emoji.EVERGREEN_TREE.getCode() + " Дебри",
            Emoji.EVERGREEN_TREE.getCode() + " Нетрі"
    ),
    LOC_CAVE(
            Emoji.BAT.getCode() + " Mythical Cave",
            Emoji.BAT.getCode() + " Таинственная Пещера",
            Emoji.BAT.getCode() + " Загадкова Печера"
    ),
    LOC_TRADING_SQUARE(
            Emoji.CIRCUS_TENT.getCode() + " Merchant Square",
            Emoji.CIRCUS_TENT.getCode() + " Торговая Площадь",
            Emoji.CIRCUS_TENT.getCode() + " Торгівельна Площа"
    ),


    // MOBS
    MOB_FLYING_SWORD(
            "Flying Sword",
            "Летающий Меч",
            "Літаюча шабля"
    ),
    MOB_SUPER_FLYING_SWORD(
            "Super Flying Sword",
            "Острый Летающий Меч",
            "Гостра Літаюча Шабля"
    ),

    // BUTTONS
    BUTTON_MOVE(
            Emoji.FOOTPRINTS.getCode() + "Go ahead",
            Emoji.FOOTPRINTS.getCode() + "В путь",
            Emoji.FOOTPRINTS.getCode() + "До перемог"
    ),
    BUTTON_SKILLS(
            Emoji.SKILLS.getCode() + "Skills",
            Emoji.SKILLS.getCode() + "Навыки",
            Emoji.SKILLS.getCode() + "Навички"
    ),
    BUTTON_ME(
            Emoji.SCROLL.getCode() + "Chronicle",
            Emoji.SCROLL.getCode() + "Летопись",
            Emoji.SCROLL.getCode() + "Літопис"
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