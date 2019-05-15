package com.wildtigerrr.StoryOfCamelot.bin.enums;

public enum MainText {
    // TUTORIAL
    LANGUAGE_SELECT(0, "Please, choose language:", "Будь-ласка, оберіть мову:"),
    MEET_NEW_PLAYER(0,
            "Ты видишь перед собой пожилого человека в фиолетовой мантии."
                    + "\n\n*Похоже, мы ещё не знакомы. Меня зовут Хранитель. Я глава Гильдии Магов этого королевства.*"
                    + "\n\nЕго голос кажется тебе смутно знакомым, но ты не можешь вспомнить, где же ты мог его слышать."
                    + "\n\n*А ты у нас кто?*"
                    + "\n\n\n_(Представьтесь)_",
            "Ти бачишь перед собою літню людину у фіолетовій мантії." +
                    "\n\n*Схоже, ми ще не знайомі. Мене звуть Хранитель. Я глава Гільдії Магів цього королівства.*" +
                    "\n\n\nЙого голос здається тобі знайомим, але ти не можеш згадати, де ж ти міг його чути." +
                    "\n\n*А ти у нас хто?*" +
                    "\n\n\n_(Назвіть себе)_"),
    NICKNAME_SET(2,
            "*p1*, да? Что ж, не мне тебя судить за это, тут как назвали. *Добро пожаловать*, как говорится." +
                    "\nРаз уж ты тут, не мог бы мне помочь с одним делом? Сходи в *p2*, узнай у местных стражников обстановку. " +
                    "Насколько я знаю, подмога им не помешает.",
            "*p1*, так? Ну, козаків і не так називали. *Ласкаво просимо*, як то кажуть." +
                    "\nЯкщо вже ти тут, не міг би мені допомогти з однією справою? Сходи в *p2*, дізнайся у місцевих охоронців становище. " +
                    "Наскільки я знаю, допомога їм не завадить."),
    GUARD_LESSON_ONE(2,
            "А, *p1*, это ты. *Хранитель* говорил, что ты придёшь. " +
                    "Да, у нас тут сейчас жарковато. Для начала, что ты умеешь? Покажи " + ReplyButton.ME.getLabel() + ".",
            "А, *p1*, це ти. *Хранитель* казав, що ти прийдеш. " +
                    "Так, у нас тут спекотно. Для початку, що ти вмієш? Покажи " + ReplyButton.ME.getLabel() + "."),
    GUARD_LESSON_TWO(0,
            "Да на тебя без слёз не взглянешь. " +
                    "\nНо у тебя есть свободные очки навыков, давай распределим их, может всё не так плохо. " +
                    "Открой " + ReplyButton.SKILLS.getLabel() + " и распредели свободные очки.",
            "Так, на тебе без сліз не поглянеш. " +
                    "\nАле в тебе є вільні навики, давай розподілимо їх, може все не так погано. " +
                    "Відкрий " + ReplyButton.SKILLS.getLabel() + " і розподіли їх."),
    TUTORIAL_NO_RUSH(0,
            "Не торопись, давай со всем по-порядку",
            "Не квапся, давай з усім по-порядку"),
    TUTORIAL_STUCK(0,
            "Туториал пошёл не по плану",
            "Туторіал йде не за планом"),

    // NICKNAME CHANGE
    NICKNAME_CHANGED(1,
            "Вы смогли переписать историю. Теперь вас будут помнить как *p1*",
            "Ви змогли переписати історію. Тепер вас будуть пам'ятати як *p1*"),
    NICKNAME_EMPTY(0,
            "*Безымянный*, да? Нет, так не пойдёт.",
            "*Безіменний*, так? Ні, так не піде."),
    NICKNAME_LONG(1,
            "Имя не может быть длиннее p1 символов.",
            "Ім'я не може бути довшим ніж p1 символів."),
    NICKNAME_WRONG(0,
            "Согласно завещанию *Древних*, имя не может содержать эти символы.",
            "Згідно із заповітом *Прадавніх*, ім'я не може містити ці символи."),
    NICKNAME_DUPLICATE(1,
            "Прошу прощения, но я уже знаю одного *p1*, ты не мог бы уточнить?",
            "Вибачаюсь, але я вже знаю одного *p1*, ти не міг би конкретизувати?"),

    // LOCATIONS & MOVEMENT,
    LOCATION_SELECT(0,
            "Итак, куда пойдём?",
            "Отже, куди будемо йти?"),
    LOCATION_SELECTED(1,
            "Ну, пойдем к *p1*",
            "Ну, ходім до *p1*"),
    LOCATION_BLOCKED(0,
            "Отсюда нет пути",
            "Звідси немає доріг"),
    NO_DIRECT(0,
            "Кажется, между этими локациями нет прямого пути",
            "Здається, між цими локаціями нема прямого шляху"),
    ALREADY_MOVING(0,
            "Вы уже в пути.",
            "Ви вже у дорозі"),
    LOCATION_ARRIVED(1,
            "p1, и что у нас тут?",
            "p1, що тут у нас?"),

    // EXPERIENCE
    STAT_UP(2,
            "Уровень p1 повышен до p2",
            "Рівень p1 виріс до p2"),
    STAT_INVALID(0,
            "Такой характеристики нет.",
            "Такої характеристики немає."),
    STAT_INSUFFICIENT_POINTS(0,
            "Не хватает свободных очков.",
            "Не вистачає вільних навиків."),
    STAT_CANNOT_BE_RAISED(1,
            "p1 не может быть повышена",
            "p1 не може бути підвищена"),
    LEVEL_UP(1,
            "Вы научились чему-то новому. Ваш уровень повышен до p1",
            "Ви навчилися чомусь новому. Ваш рівень виріс до p1"),

    COMMAND_INVALID(0,
            "Кажется, где-то здесь ошибка.",
            "Здається, тут є помилка."),
    COMMAND_UNKNOWN(0,
            "Я не знаю что мне делать с этою бедой...",
            "Що ж мені з цим робити..."),
    COMMAND_NOT_DEFINED(0,
            "Слушай, я о чем-то таком слышал, но почему-то не знаю что делать",
            "Слухай, я про щось таке чув, проте не знаю що з цим робити"),
    COMMAND_NOT_DEVELOPED(0,
            "Увы, эта функция ещё в разработке =( \n\n *Но скоро всё будет!*",
            "Нажаль, ця функція ще у розробці =( \n\n *Але скоро буде тут!*"),
    PROPOSITION_EXPIRED(0,
            "Это уже неактуально",
            "Спізнився ти, хлопче"),
    PLAYER_NOT_EXIST(0,
            "You don't have a player yet.",
            "You don't have a player yet.");

    private final String textRUS;
    private final String textUKR;
    private final int parameters;

    MainText(int parameters, String textRUS, String textUKR) {
        this.parameters = parameters;
        this.textRUS = textUKR;
        this.textUKR = textRUS;
    }

    public String text(Language lang) {
        if (parameters == 0) {
            switch (lang) {
                case RUS:
                    return textRUS;
                case UKR:
                    return textUKR;
                default:
                    return "No such language";
            }
        } else
            return "Wrong parameters quantity";
    }

    public String text(Language lang, String param) {
        if (parameters == 1) {
            switch (lang) {
                case RUS:
                    return textRUS.replace("p1", param);
                case UKR:
                    return textUKR.replace("p1", param);
                default:
                    return "No such language";
            }
        } else
            return "Wrong parameters quantity";
    }

    public String text(Language lang, String param1, String param2) {
        if (parameters == 2) {
            switch (lang) {
                case RUS:
                    return textRUS.replace("p1", param1).replace("p2", param2);
                case UKR:
                    return textUKR.replace("p1", param1).replace("p2", param2);
                default:
                    return "No such language";
            }
        } else
            return "Wrong parameters quantity";
    }
}
