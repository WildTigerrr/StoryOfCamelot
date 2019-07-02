package com.wildtigerrr.StoryOfCamelot.bin.translation;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;

public interface Translation {

    Language getCurrentLanguage();

    static String replaceOne(String text, String p1) {
        return text.replace("p1", p1);
    }

    static String replaceTwo(String text, String p1, String p2) {
        return text.replace("p1", p1).replace("p2", p2);
    }

    String languageSelectPrompt();

    String languageSelected();



    // TUTORIAL
    String tutorialMeetNewPlayer();

    String tutorialNicknameSet(String nickname, String locationName);

    String tutorialGuardLessonOne(String nickname);

    String tutorialGuardLessonTwo();

    String tutorialGuardLessonThree();

    String tutorialNoRush();

    String tutorialStuck();



    // PHRASES
    String ifIRemember();

    String whatElseWeKnow();



    // NICKNAME CHANGE
    String nicknameChanged(String newNickname);

    String nicknameEmpty();

    String nicknameTooLong(String maxLength);

    String nicknameWrongSymbols();

    String nicknameDuplicate(String newNickname);



    // MOVEMENT
    String locationSelect();

    String locationAccepted(String newLocation);

    String locationBlocked();

    String locationNotConnected();

    String locationArrived(String newLocation);

    String locationChangeInProgress();



    // EXPERIENCE
    String level();

    String statUp(String statName, String newValue);

    String statInvalid();

    String statInsufficientPoints();

    String statCannotBeRaised(String invalidStat);

    String levelUp(String currentLevel);



    // RESPONSES
    String commandInvalid();

    String commandUnknown();

    String commandNotDefined();

    String commandNotDeveloped();

    String propositionExpired();

    String playerNotExist();
}
