package com.wildtigerrr.StoryOfCamelot.bin.base.service.player;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.EditResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class ExperienceService {

    private final ResponseManager messages;
    private final TranslationManager translation;
    private final PlayerServiceImpl playerService;

    @Autowired
    public ExperienceService(ResponseManager messages, TranslationManager translation, PlayerServiceImpl playerService) {
        this.messages = messages;
        this.translation = translation;
        this.playerService = playerService;
    }

    public void sendSkillWindow(Player player) {
        messages.sendMessage(TextResponseMessage.builder()
                .text(player.getStatMenu(translation))
                .keyboard(KeyboardManager.getKeyboardForStatUp(player.stats().getUnassignedPoints()))
                .targetId(player).build()
        );
    }

    public Player addExperience(Player player, Stats stat, int experience, Boolean sendExperienceGet) {
        if (experience == 0) return player;
        try {
            List<String> eventList = player.stats().addStatExp(
                    experience,
                    stat,
                    player.getLanguage(),
                    translation
            );
            if (eventList != null && !eventList.isEmpty()) {
                for (String event : eventList) {
                    if (event != null && !event.equals("")) {
                        messages.sendMessage(TextResponseMessage.builder().lang(player)
                                .text(event).targetId(player).build()
                        );
                    }
                }
            }
            if (sendExperienceGet) {
                messages.sendMessage(TextResponseMessage.builder().lang(player)
                        .text(translation.getMessage("player.stats.experience-taken", player.getLanguage(),
                                new Object[]{experience}))
                        .targetId(player).build()
                );
            }
            return player;
        } catch (InvalidInputException e) {
            messages.sendErrorReport(e);
        }
        return player;
    }

    public void statUp(UpdateWrapper message) {
        String[] commandParts = message.getText().split("_", 3);
        if (commandParts.length == 3 && commandParts[1].length() == 1 && StringUtils.isNumeric(commandParts[2])) {
            Stats stat = Stats.getStat(commandParts[1]);
            if (stat == null) {
                messages.sendMessage(TextResponseMessage.builder()
                        .text(translation.getMessage("player.stats.invalid", message)).targetId(message).build()
                );
            } else {
                Player player = message.getPlayer();
                String result = player.stats().raiseStat(stat, Integer.valueOf(commandParts[2]), player.getLanguage(), translation);
                if (!result.equals(translation.getMessage("player.stats.invalid", message)))
                    playerService.update(player);
                messages.sendAnswer(message.getQueryId(), result);
                if (player.stats().getUnassignedPoints() == 0) {
                    messages.sendMessage(EditResponseMessage.builder()
                            .messageId(message).text(player.getStatMenu(translation)).targetId(player).build()
                    );
                } else {
                    messages.sendMessage(EditResponseMessage.builder()
                            .messageId(message)
                            .text(player.getStatMenu(translation))
                            .keyboard(KeyboardManager.getKeyboardForStatUp(player.stats().getUnassignedPoints()))
                            .targetId(player).build()
                    );
                }
            }
        } else {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(translation.getMessage("commands.invalid", message)).targetId(message).build()
            );
        }
    }

    private void handleError(String message, Exception e) {
        log.error(message, e);
        messages.postMessageToAdminChannel(message);
    }

}
