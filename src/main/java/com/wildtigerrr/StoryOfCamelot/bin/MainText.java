package com.wildtigerrr.StoryOfCamelot.bin;

public enum MainText {
    HELLO (
            "Эй, ты новенький?"
    ),
    MEET_NEW_PLAYER (
            "Ты видишь перед собой пожилого человека в фиолетовой мантии."
                    + "\n\n*Похоже, мы ещё не знакомы. Меня зовут Хранитель. Я глава Гильдии Магов этого королевства.*"
                    + "\n\nЕго голос кажется тебе смутно знакомым, но ты не можешь вспомнить, где же ты мог его слышать."
                    + "\n\n*А ты у нас кто?*"
                    + "\n\n\n_(Представьтесь)_"
    ),
    UNKNOWN_COMMAND (
            "Я не знаю что мне делать с этою бедой..."
    ),
    EMPTY_NICKNAME (
            "*Безымянный, да? Нет, так не пойдёт.*"
    ),
    COMMAND_NOT_DEFINED ("Слушай, я о чем-то таком слышал, но почему-то не знаю что делать"),
    NICKNAME_CHANGED (
            "Вы смогли переписать историю. Теперь вас будут помнить как *"
    ),
    PLAYER_NOT_EXIST (
            "You don't have a player yet."
    );
    private final String text;

    MainText(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
