package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum MainText {
    HELLO(0, "Эй, ты новенький?"),
    MEET_NEW_PLAYER(0, "Ты видишь перед собой пожилого человека в фиолетовой мантии."
            + "\n\n*Похоже, мы ещё не знакомы. Меня зовут Хранитель. Я глава Гильдии Магов этого королевства.*"
            + "\n\nЕго голос кажется тебе смутно знакомым, но ты не можешь вспомнить, где же ты мог его слышать."
            + "\n\n*А ты у нас кто?*"
            + "\n\n\n_(Представьтесь. Допустимы буквы и пробелы)_"),
    UNKNOWN_COMMAND(0, "Я не знаю что мне делать с этою бедой..."),
    COMMAND_NOT_DEFINED(0, "Слушай, я о чем-то таком слышал, но почему-то не знаю что делать"),
    NICKNAME_SET(2, "*p1*, да? Что ж, не мне тебя судить за это. *Добро пожаловать*, как говориться." +
            "\nРаз уж ты тут, не мог бы мне помочь с одним делом? Сходи в *p2*, спроси у местных стражников как обстановка."),
    NICKNAME_CHANGED(1, "Вы смогли переписать историю. Теперь вас будут помнить как *p1*"),
    NICKNAME_EMPTY(0, "*Безымянный*, да? Нет, так не пойдёт."),
    NICKNAME_DUPLICATE(1, "Прошу прощения, но я уже знаю одного *p1*, ты не мог бы уточнить?"),
    PLAYER_NOT_EXIST(0, "You don't have a player yet."),

    // LOCATIONS & MOVEMENT,
    NO_DIRECT(0, "Кажется, между этими локациями нет прямого пути"),
    ALREADY_MOVING(0, "Вы уже в пути."),
    LOCATION_HELLO(1, "p1, и что у нас тут?"),

    // EXPERIENCE
    STAT_UP(2, "Уровень p1 повышен до p2"),
    LEVEL_UP(1, "Вы научились чему-то новому. Ваш уровень повышен до p1");

    private final String text;
    private final int parameters;

    MainText(int parameters, String text) {
        this.parameters = parameters;
        this.text = text;
    }

    public String text() {
        if (parameters == 0)
            return text;
        else
            return "Wrong parameters quantity";
    }

    public String text(String param) {
        if (parameters == 1)
            return text.replace("p1", param);
        else
            return "Wrong parameters quantity";
    }

    public String text(String param1, String param2) {
        if (parameters == 2)
            return text.replace("p1", param1).replace("p2", param2);
        else
            return "Wrong parameters quantity";
    }
}
