package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum MainText {
    HELLO("Эй, ты новенький?"),
    MEET_NEW_PLAYER("Ты видишь перед собой пожилого человека в фиолетовой мантии."
            + "\n\n*Похоже, мы ещё не знакомы. Меня зовут Хранитель. Я глава Гильдии Магов этого королевства.*"
            + "\n\nЕго голос кажется тебе смутно знакомым, но ты не можешь вспомнить, где же ты мог его слышать."
            + "\n\n*А ты у нас кто?*"
            + "\n\n\n_(Представьтесь. Допустимы буквы и пробелы)_"),
    UNKNOWN_COMMAND("Я не знаю что мне делать с этою бедой..."),
    COMMAND_NOT_DEFINED("Слушай, я о чем-то таком слышал, но почему-то не знаю что делать"),
    NICKNAME_CHANGED("Вы смогли переписать историю. Теперь вас будут помнить как *"),
    NICKNAME_EMPTY("*Безымянный*, да? Нет, так не пойдёт."),
    NICKNAME_DUPLICATE_START("Прошу прощения, но я уже знаю одного *"),
    NICKNAME_DUPLICATE_END("*, ты не мог бы уточнить?"),
    PLAYER_NOT_EXIST("You don't have a player yet."),

    // LOCATIONS & MOVEMENT,
    NO_DIRECT("Кажется, между этими локациями нет прямого пути"),
    ALREADY_MOVING("Вы уже в пути."),
    LOCATION_HELLO(", и что у нас тут?"),

    // EXPERIENCE
    STAT_UP_START("Уровень "), STAT_UP_END(" повышен до "),
    LEVEL_UP("Вы научились чему-то новому. Ваш уровень повышен до ");

    private final String text;

    MainText(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
