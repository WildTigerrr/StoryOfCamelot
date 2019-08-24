package com.wildtigerrr.StoryOfCamelot.bin.translation;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;

public class TranslationUkr implements Translation {

    @Override
    public Language getCurrentLanguage() {
        return Language.UKR;
    }

    @Override
    public String languageSelectPrompt() {
        return
                "Будь-ласка, оберіть мову:";
    }

    @Override
    public String languageSelected() {
        return
                "Встановлено мову " + Language.UKR.getName();
    }

    @Override
    public String tutorialMeetNewPlayer() {
        return
                "Ти бачишь перед собою літню людину у фіолетовій мантії." +
                        "\n\n*Схоже, ми ще не знайомі. Мене звуть Хранитель. Я глава Гільдії Магів цього королівства.*" +
                        "\n\n\nЙого голос здається тобі знайомим, але ти не можеш згадати, де ж ти міг його чути." +
                        "\n\n*А ти у нас хто?*" +
                        "\n\n\n_(Назвіть себе)_";
    }

    @Override
    public String tutorialNicknameSet(String nickname, String locationName) {
        return Translation.replaceTwo(
                "*p1*, так? Ну, козаків і не так називали. *Ласкаво просимо*, як то кажуть." +
                        "\nЯкщо вже ти тут, не міг би мені допомогти з однією справою? Сходи в *p2*, дізнайся у місцевих охоронців становище. " +
                        "Наскільки я знаю, допомога їм не завадить.",
                nickname, locationName
        );
    }

    @Override
    public String tutorialGuardLessonOne(String nickname) {
        return Translation.replaceOne(
                "А, *p1*, це ти. *Хранитель* казав, що ти прийдеш. " +
                        "Так, у нас тут спекотно. Для початку, що ти вмієш? Покажи " + ReplyButton.ME.getLabel(Language.UKR) + ".",
                nickname
        );
    }

    @Override
    public String tutorialGuardLessonTwo() {
        return
                "Так, на тебе без сліз не поглянеш. " +
                        "\nАле в тебе є вільні навики, давай розподілимо їх, може все не так погано. " +
                        "Відкрий " + ReplyButton.SKILLS.getLabel(Language.UKR) + " і розподіли їх.";
    }

    @Override
    public String tutorialGuardLessonThree() {
        return
                "Ось, теперь справжній козак. Ну, майже. Для початку непогано." +
                        "\n\nТеперь потрібно перевірити тебе у справжньому бою. " +
                        "Здається, цей " + NameTranslation.MOB_FLYING_SWORD.getName(getCurrentLanguage()) + " підкрадається до тебе не просто так.";
    }

    @Override
    public String tutorialNoRush() {
        return
                "Не квапся, давай з усім по-порядку";
    }

    @Override
    public String tutorialStuck() {
        return
                "Туторіал пішов не за планом";
    }

    @Override
    public String ifIRemember() {
        return
                "Якщо я себе не переоцінюю, то";
    }

    @Override
    public String whatElseWeKnow() {
        return
                "Що ж іще додати";
    }

    @Override
    public String nicknameChanged(String newNickname) {
        return Translation.replaceOne(
                "Ви змогли переписати історію. Тепер вас будуть пам'ятати як *p1*",
                newNickname
        );
    }

    @Override
    public String nicknameEmpty() {
        return
                "*Безіменний*, так? Ні, так не піде.";
    }

    @Override
    public String nicknameTooLong(String maxLength) {
        return Translation.replaceOne(
                "Ім'я не може бути довшим ніж p1 символів.",
                maxLength
        );
    }

    @Override
    public String nicknameWrongSymbols() {
        return
                "Згідно із заповітом *Прадавніх*, ім'я не може містити ці символи.";
    }

    @Override
    public String nicknameDuplicate(String newNickname) {
        return Translation.replaceOne(
                "Вибачаюсь, але я вже знаю одного *p1*, ти не міг би конкретизувати?",
                newNickname
        );
    }

    @Override
    public String locationSelect() {
        return
                "Отже, куди будемо йти?";
    }

    @Override
    public String locationAccepted(String newLocation) {
        return Translation.replaceOne(
                "Ну, ходім до *p1*",
                newLocation
        );
    }

    @Override
    public String locationBlocked() {
        return
                "Звідси немає доріг";
    }

    @Override
    public String locationNotConnected() {
        return
                "Здається, між цими локаціями нема прямого шляху";
    }

    @Override
    public String locationArrived(String newLocation) {
        return Translation.replaceOne(
                "p1, що тут у нас?",
                newLocation
        );
    }

    @Override
    public String locationChangeInProgress() {
        return
                "Ви вже у дорозі";
    }

    @Override
    public String level() {
        return
                "Рівень";
    }

    @Override
    public String statUp(String statName, String newValue) {
        return Translation.replaceTwo(
                "Рівень p1 виріс до p2",
                statName, newValue
        );
    }

    @Override
    public String statInvalid() {
        return
                "Характеристику не знайдено.";
    }

    @Override
    public String statInsufficientPoints() {
        return
                "Не вистачає вільних навиків.";
    }

    @Override
    public String statCannotBeRaised(String invalidStat) {
        return Translation.replaceOne(
                "p1 не може бути підвищена",
                invalidStat
        );
    }

    @Override
    public String levelUp(String currentLevel) {
        return Translation.replaceOne(
                "Ви навчилися чомусь новому. Ваш рівень виріс до p1",
                currentLevel
        );
    }

    @Override
    public String commandInvalid() {
        return
                "Здається, тут є помилка.";
    }

    @Override
    public String commandUnknown() {
        return
                "Що ж мені з цим робити...";
    }

    @Override
    public String commandNotDefined() {
        return
                "Слухай, я про щось таке чув, проте не знаю що з цим робити";
    }

    @Override
    public String commandNotDeveloped() {
        return
                "Нажаль, ця функція ще у розробці =( " +
                        "\n\n *Але скоро буде тут!*";
    }

    @Override
    public String propositionExpired() {
        return
                "Спізнився ти, хлопче";
    }

    @Override
    public String playerNotExist() {
        return
                "У вас ще немає гравця.";
    }

}
