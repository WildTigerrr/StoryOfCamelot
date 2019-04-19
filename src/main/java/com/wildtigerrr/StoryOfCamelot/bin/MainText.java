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
                    + "\n\nЕго голос кажется тебе смутно знакомым, но ты не можешь вспомнить, где же ты мог его слышать."
                    + "\n\n*А ты у нас кто?*"
                    + "\n\n\n_(Представьтесь)_";
        }
    },
    UNKNOWN_COMMAND {
        @Override
        public String text() {
            return "Я не знаю что мне делать с этою бедой...";
        }
    },
    EMPTY_NICKNAME {
        @Override
        public String text() {
            return "*Безымянный, да? Нет, так не пойдёт.*";
        }
    },
    COMMAND_NOT_DEFINED {
        @Override
        public String text() {
            return "Слушай, я о чем-то таком слышал, но почему-то не знаю что делать";
        }
    },
    NICKNAME_CHANGED {
        @Override
        public String text() {
            return "Вы смогли переписать историю. Теперей вас будут помнить как ";
        }
    },
    PLAYER_NOT_EXIST {
        @Override
        public String text() {
            return "You don't have a player yet.";
        }
    };
    abstract public String text();
}
