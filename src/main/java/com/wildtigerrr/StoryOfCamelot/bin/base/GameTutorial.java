package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.*;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import com.wildtigerrr.StoryOfCamelot.web.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class GameTutorial {

    @Autowired
    private ResponseManager messages;
    @Autowired
    private GameMain gameMain;
    @Autowired
    private PlayerServiceImpl playerService;
    @Autowired
    private LocationServiceImpl locationService;

    public Boolean proceedTutorial(UpdateWrapper message) {
        Command command = ResponseHandler.messageToCommand(message.getText(), message.getPlayer().getLanguage());
        switch (message.getPlayer().getAdditionalStatus()) {
            case LANGUAGE_CHOOSE:
                if (!message.isQuery() && (command == Command.START || message.getPlayer().isNew())) {
                    Language lang = Language.ENG;
                    String langCode = message.getLanguage().substring(0, Math.min(message.getLanguage().length(), 2));
                    // List - https://datahub.io/core/language-codes/r/3.html
                    if (langCode.equals("ru")) lang = Language.RUS;
                    else if (langCode.equals("uk")) lang = Language.UKR;
                    gameMain.sendLanguageSelector(message.getUserId(), lang);
                } else if (message.isQuery() && command == Command.LANG) {
                    String[] commandParts = message.getText().split(" ", 2);
                    if (StringUtils.isNumeric(commandParts[1]) && Integer.valueOf(commandParts[1]) < Language.values().length) {
                        Player player = message.getPlayer();
                        player.setLanguage(Language.values()[Integer.valueOf(commandParts[1])]);
                        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_NICKNAME);
                        messages.sendMessageEdit(
                                message.getMessageId(),
                                MainText.LANGUAGE_SELECTED.text(player.getLanguage()),
                                message.getUserId(),
                                true
                        );
                        tutorialStart(player);
                    } else {
                        messages.sendMessage(MainText.COMMAND_INVALID.text(message.getPlayer().getLanguage()), message.getUserId());
                    }
                } else {
                    messages.sendMessage(MainText.TUTORIAL_NO_RUSH.text(message.getPlayer().getLanguage()), message.getUserId());
                }
                break;
            case TUTORIAL_NICKNAME:
                tutorialNickname(message.getPlayer(), message.getText());
                break;
            case TUTORIAL_MOVEMENT:
                if (command == Command.MOVE) {
                    return false;
                } else {
                    messages.sendMessage(MainText.TUTORIAL_NO_RUSH.text(message.getPlayer().getLanguage()), message.getUserId());
                }
                break;
            case TUTORIAL_STATS:
                if (command == Command.ME) {
                    messages.sendMessage(message.getPlayer().toString(), message.getUserId(), true);
                    tutorialStats(message.getPlayer());
                } else {
                    messages.sendMessage(MainText.TUTORIAL_NO_RUSH.text(message.getPlayer().getLanguage()), message.getUserId());
                }
                break;
            case TUTORIAL_STATS_UP:
                if (command == Command.SKILLS) {
                    tutorialStatsUp(message.getPlayer());
//                    messages.sendMessage(MainText.TUTORIAL_STUCK.text(message.getPlayer().getLanguage()), message.getUserId());
                } else {
                    messages.sendMessage(MainText.TUTORIAL_NO_RUSH.text(message.getPlayer().getLanguage()), message.getUserId());
                }
                break;
            case TUTORIAL_STATS_UP_2:
                if (command == Command.UP) {
                    gameMain.statUp(message);
                } else {
                    messages.sendMessage(MainText.TUTORIAL_NO_RUSH.text(message.getPlayer().getLanguage()), message.getUserId());
                }
            default:
                messages.sendMessage(MainText.COMMAND_NOT_DEVELOPED.text(message.getPlayer().getLanguage()), message.getUserId());
        }
        return true;
    }

    public void tutorialStart(Player player) {
        System.out.println("New player");
        player.setup();
        playerService.update(player);
        messages.sendMessage(
                MainText.MEET_NEW_PLAYER.text(player.getLanguage()),
                player.getExternalId(),
                true
        );
    }

    private void tutorialNickname(Player player, String nickname) {
        gameMain.setNickname(player, nickname);
    }

    void tutorialSetNickname(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_MOVEMENT);
        playerService.update(player);
        messages.sendMessage(
                MainText.NICKNAME_SET.text(
                        player.getLanguage(),
                        player.getNickname(),
                        locationService.findByName(GameSettings.FIRST_FOREST_LOCATION.get()).getName(player.getLanguage())
                ),
                KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.MOVE)), player.getLanguage()),
                player.getExternalId()
        );
    }

    void tutorialMovement(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS);
        playerService.update(player);
        ArrayList<ReplyButton> buttons = new ArrayList<>();
        buttons.add(ReplyButton.ME);
        buttons.add(ReplyButton.MOVE);
        messages.sendMessage(
                MainText.GUARD_LESSON_ONE.text(
                        player.getLanguage(),
                        player.getNickname()
                ),
                KeyboardManager.getReplyByButtons(buttons, player.getLanguage()),
                player.getExternalId()
        );
    }

    public void tutorialStats(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS_UP);
        playerService.update(player);
        messages.sendMessage(
                MainText.GUARD_LESSON_TWO.text(player.getLanguage()),
                KeyboardManager.getReplyByButtons(new ArrayList<>(Collections.singleton(ReplyButton.SKILLS)), player.getLanguage()),
                player.getExternalId()
        );
    }

    public void tutorialStatsUp(Player player) {
        player.setAdditionalStatus(PlayerStatusExtended.TUTORIAL_STATS_UP_2);
        playerService.update(player);
        messages.sendMessage(getStatMenu(player), player.getExternalId());
    }

    private String getStatMenu(Player player) {
        int unassigned = player.getUnassignedPoints();
        return "*" + player.getNickname() + "*, " + player.getLevel() + " (+" + unassigned + ")"
                + "\n\n" + Emoji.STRENGTH.getCode() + "Cила: " + player.getStrength()
                + "\n" + "/up_s_1 " + (unassigned > 4 ? "/up_s_5 " : "") + "/up_s_" + player.getUnassignedPoints()
                + "\n" + Emoji.HEALTH.getCode() + "Здоровье: " + player.getHealth()
                + "\n" + "/up_h_1 " + (unassigned > 4 ? "/up_h_5 " : "") + "/up_h_" + player.getUnassignedPoints()
                + "\n" + Emoji.AGILITY.getCode() + "Ловкость: " + player.getAgility()
                + "\n" + "/up_a_1 " + (unassigned > 4 ? "/up_a_5 " : "") + "/up_a_" + player.getUnassignedPoints()
                + "\n" + Emoji.CHARISMA.getCode() + "Харизма: " + player.getCharisma()
                + "\n" + "/up_c_1 " + (unassigned > 4 ? "/up_c_5 " : "") + "/up_c_" + player.getUnassignedPoints()
                + "\n" + Emoji.INTELLIGENCE.getCode() + "Интеллект: " + player.getIntelligence()
                + "\n" + "/up_i_1 " + (unassigned > 4 ? "/up_i_5 " : "") + "/up_i_" + player.getUnassignedPoints()
                + "\n" + Emoji.ENDURANCE.getCode() + "Выносливость: " + player.getEndurance()
                + "\n" + "/up_e_1 " + (unassigned > 4 ? "/up_e_5 " : "") + "/up_e_" + player.getUnassignedPoints()
                + "\n" + Emoji.LUCK.getCode() + "Удача: " + player.getLuck()
                + "\n" + "/up_l_1 " + (unassigned > 4 ? "/up_l_5 " : "") + "/up_l_" + player.getUnassignedPoints()
                + "\n\nТакже можно ввести любое число в заданном формате, не более " + unassigned + "."

                ;
    }

}
