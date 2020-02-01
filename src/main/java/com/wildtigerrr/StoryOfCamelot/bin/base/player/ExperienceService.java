package com.wildtigerrr.StoryOfCamelot.bin.base.player;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
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

    public void addStatPoints(UpdateWrapper update) {
        Player player = update.getPlayer();
        String[] values = update.getText().split(" ", 3);
        try {
            player = addExperience(
                    player,
                    Stats.valueOf(values[1].toUpperCase()),
                    Integer.parseInt(values[2]),
                    true
            );
            playerService.update(player);
        } catch (IllegalArgumentException e) {
            handleError("Wrong Stat" + values[1].toUpperCase(), e);
        }
    }

    public Player addExperience(Player player, Stats stat, int experience, Boolean sendExperienceGet) {
        try {
            List<String> eventList = player.addStatExp(
                    experience,
                    stat,
                    player.getLanguage(),
                    translation
            );
            if (eventList != null && !eventList.isEmpty()) {
                for (String event : eventList) {
                    if (event != null && !event.equals("")) {
                        messages.sendMessage(TextResponseMessage.builder()
                                .text(event).targetId(player).build()
                        );
                    }
                }
            }
            if (sendExperienceGet) {
                messages.sendMessage(TextResponseMessage.builder()
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

    private void handleError(String message, Exception e) {
        log.error(message, e);
        messages.postMessageToAdminChannel(message);
    }

}
