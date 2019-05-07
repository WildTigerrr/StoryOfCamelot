package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.exceptions.SOCInvalidDataException;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GameMain {

    @Autowired
    private ResponseManager messages;

    public Player addExperience(Player player, Stats stat, int experience, Boolean sendExperienceGet) {
        try {
            ArrayList<String> eventList = player.addStatExp(
                    experience,
                    stat
            );
            if (eventList != null && !eventList.isEmpty()) {
                for (String event : eventList) {
                    if (event != null && !event.equals("")) {
                        messages.sendMessage(event, player.getExternalId());
                    }
                }
            }
            if (sendExperienceGet) {
                messages.sendMessage("Очков опыта получено: " + experience, player.getExternalId());
            }
            return player;
        } catch (SOCInvalidDataException e) {
            messages.sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
        return player;
    }

}
