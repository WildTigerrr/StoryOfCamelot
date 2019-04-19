package com.wildtigerrr.StoryOfCamelot.bin;

public enum MainText {
    HELLO {
        @Override
        public String text() {
            return "Эй, ты новенький?";
        }
    },
    MEET_NEW_PLAYER {
        @Override
        public String text() {
            return "Ты видишь перед собой пожилого человека в фиолетовой мантии."
                    + "\n\n*Похоже, мы ещё не знакомы. Меня зовут Хранитель. Я глава Гильдии Магов этого королевства.*"
                    + "\n\nЕго голос кажется тебе смутно знакомым, но ты не можешь вспомнить где же ты мог его слышать."
                    + "\n\n*А ты у нас кто?*"
                    + "\n\n\n_(Ваше имя)_";
        }
    };
    abstract public String text();
}
