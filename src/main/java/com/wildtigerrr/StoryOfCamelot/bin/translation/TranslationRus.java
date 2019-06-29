package com.wildtigerrr.StoryOfCamelot.bin.translation;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;

public class TranslationRus implements Translation {

    @Override
    public String languageSelectPrompt() {
        return
                "Пожалуйста, выберите язык:";
    }

    @Override
    public String languageSelected() {
        return
                "Установлен язык " + Language.RUS.getName();
    }

    @Override
    public String tutorialMeetNewPlayer() {
        return
                "Ты видишь перед собой пожилого человека в фиолетовой мантии."
                        + "\n\n*Похоже, мы ещё не знакомы. Меня зовут Хранитель. Я глава Гильдии Магов этого королевства.*"
                        + "\n\nЕго голос кажется тебе смутно знакомым, но ты не можешь вспомнить, где же ты мог его слышать."
                        + "\n\n*А ты у нас кто?*"
                        + "\n\n\n_(Представьтесь)_";
    }

    @Override
    public String tutorialNicknameSet(String nickname, String locationName) {
        return Translation.replaceTwo(
                "*p1*, да? Что ж, не мне тебя судить за это, тут как назвали. *Добро пожаловать*, как говорится." +
                        "\nРаз уж ты тут, не мог бы мне помочь с одним делом? Сходи в *p2*, узнай у местных стражников обстановку. " +
                        "Насколько я знаю, подмога им не помешает.",
                nickname, locationName
        );
    }

    @Override
    public String tutorialGuardLessonOne(String nickname) {
        return Translation.replaceOne(
                "А, *p1*, это ты. *Хранитель* говорил, что ты придёшь. " +
                        "Да, у нас тут сейчас жарковато. Для начала, что ты умеешь? Покажи " + ReplyButton.ME.getLabel(Language.RUS) + ".",
                nickname
        );
    }

    @Override
    public String tutorialGuardLessonTwo() {
        return
                "Да на тебя без слёз не взглянешь. " +
                        "\nНо у тебя есть свободные очки навыков, давай распределим их, может всё не так плохо. " +
                        "Открой " + ReplyButton.SKILLS.getLabel(Language.RUS) + " и распредели свободные очки.";
    }

    @Override
    public String tutorialGuardLessonThree() {
        return
                "Готово, другое дело. На человека даже стал похож. Ну, немного. Ладно, для начала и так сойдёт." +
                        "\n\nТеперь нужно бы проверить тебя в бою. Подожди немного, доделаем функцию боя.";
    }

    @Override
    public String tutorialNoRush() {
        return
                "Не торопись, давай со всем по-порядку";
    }

    @Override
    public String tutorialStuck() {
        return
                "Туториал пошёл не по плану";
    }

    @Override
    public String ifIRemember() {
        return
                "Если память тебя не подводит, то";
    }

    @Override
    public String whatElseWeKnow() {
        return
                "Что же ещё известно";
    }

    @Override
    public String nicknameChanged(String newNickname) {
        return Translation.replaceOne(
                "Вы смогли переписать историю. Теперь вас будут помнить как *p1*",
                newNickname
        );
    }

    @Override
    public String nicknameEmpty() {
        return
                "*Безымянный*, да? Нет, так не пойдёт.";
    }

    @Override
    public String nicknameTooLong(String maxLength) {
        return Translation.replaceOne(
                "Имя не может быть длиннее p1 символов.",
                maxLength
        );
    }

    @Override
    public String nicknameWrongSymbols() {
        return
                "Согласно завещанию *Древних*, имя не может содержать эти символы.";
    }

    @Override
    public String nicknameDuplicate(String newNickname) {
        return
                "Прошу прощения, но я уже знаю одного *p1*, ты не мог бы уточнить?";
    }

    @Override
    public String locationSelect() {
        return
                "Итак, куда пойдём?";
    }

    @Override
    public String locationAccepted(String newLocation) {
        return Translation.replaceOne(
                "Ну, пойдем к *p1*",
                newLocation
        );
    }

    @Override
    public String locationBlocked() {
        return
                "Отсюда нет пути";
    }

    @Override
    public String locationNotConnected() {
        return
                "Кажется, между этими локациями нет прямого пути";
    }

    @Override
    public String locationArrived(String newLocation) {
        return Translation.replaceOne(
                "p1, и что у нас тут?",
                newLocation
        );
    }

    @Override
    public String locationChangeInProgress() {
        return
                "Вы уже в пути.";
    }

    @Override
    public String level() {
        return
                "Уровень";
    }

    @Override
    public String statUp(String statName, String newValue) {
        return Translation.replaceTwo(
                "Уровень p1 повышен до p2",
                statName, newValue
        );
    }

    @Override
    public String statInvalid() {
        return
                "Такой характеристики нет.";
    }

    @Override
    public String statInsufficientPoints() {
        return
                "Не хватает свободных очков.";
    }

    @Override
    public String statCannotBeRaised(String invalidStat) {
        return Translation.replaceOne(
                "p1 не может быть повышена",
                invalidStat
        );
    }

    @Override
    public String levelUp(String currentLevel) {
        return Translation.replaceOne(
                "Вы научились чему-то новому. Ваш уровень повышен до p1",
                currentLevel
        );
    }

    @Override
    public String commandInvalid() {
        return
                "Кажется, где-то здесь ошибка.";
    }

    @Override
    public String commandUnknown() {
        return
                "Я не знаю что мне делать с этою бедой...";
    }

    @Override
    public String commandNotDefined() {
        return
                "Слушай, я о чем-то таком слышал, но почему-то не знаю что делать";
    }

    @Override
    public String commandNotDeveloped() {
        return
                "Увы, эта функция ещё в разработке =( " +
                        "\n\n *Но скоро всё будет!*";
    }

    @Override
    public String propositionExpired() {
        return
                "Это уже неактуально";
    }

    @Override
    public String playerNotExist() {
        return
                "У вас ещё нет игрока.";
    }

}
