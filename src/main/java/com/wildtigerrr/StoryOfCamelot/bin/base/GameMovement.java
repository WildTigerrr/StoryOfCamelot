package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.player.ExperienceService;
import com.wildtigerrr.StoryOfCamelot.bin.service.ScheduledAction;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.LocationNearServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.service.DataProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.EditResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.ImageResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

@Service
@Log4j2
public class GameMovement {

    private ResponseManager messages;
    private DataProvider dataProvider;
    private TranslationManager translation;

    private PlayerServiceImpl playerService;
    private ExperienceService experienceService;
    private LocationServiceImpl locationService;
    private LocationNearServiceImpl locationNearService;

    public GameMovement() {
    }

    @Autowired
    GameMovement(
            DataProvider dataProvider,
            ResponseManager messages,
            TranslationManager translation,

            PlayerServiceImpl playerService,
            ExperienceService experienceService,
            LocationServiceImpl locationService,
            LocationNearServiceImpl locationNearService
            ) {
        this.dataProvider = dataProvider;
        this.messages = messages;
        this.translation = translation;

        this.playerService = playerService;
        this.locationService = locationService;
        this.experienceService = experienceService;
        this.locationNearService = locationNearService;
    }

    public void handleMove(IncomingMessage message) {
        log.warn(message.getPlayer().getStatus());
        if (message.getPlayer().getStatus() == PlayerStatus.MOVEMENT) {
            if (message.isQuery()) {
                messages.sendMessage(EditResponseMessage.builder().lang(message)
                        .messageId(message)
                        .text(translation.getMessage("movement.location.in-progress", message))
                        .targetId(message)
                        .applyMarkup(true).build()
                );
            } else {
                messages.sendMessage(TextResponseMessage.builder().lang(message)
                        .text(translation.getMessage("movement.location.in-progress", message)).targetId(message).build()
                );
            }
        } else if (message.isQuery()) {
            String[] commandParts = message.text().split(" ", 2);
            moveToLocation(message, commandParts[1]);
        } else { // if (commandParts.length < 2)
            sendAvailableLocations(message.getPlayer());
        }
    }

    public void sendAvailableLocations(Player player) {
        ArrayList<Location> nearLocations = locationNearService.getNearLocations(player.getLocation());
        if (!nearLocations.isEmpty()) {
            messages.sendMessage(TextResponseMessage.builder().lang(player)
                            .text(translation.getMessage("movement.location.select", player))
                            .keyboard(KeyboardManager.getKeyboardForLocations(nearLocations, player.getLanguage()))
                            .targetId(player).build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder().lang(player)
                    .text(translation.getMessage("movement.location.blocked", player)).targetId(player).build()
            );
        }
    }

    public void moveToLocation(IncomingMessage message, String locationId) {
        Location location = locationService.findById(locationId);
        if (location != null) {
            int distance = locationNearService.getDistance(
                    message.getPlayer().getLocation(),
                    location
            );
            if (distance == -1) {
                messages.sendMessage(TextResponseMessage.builder().lang(message)
                        .text(translation.getMessage("movement.location.no-connection", message)).targetId(message).build()
                );
                return;
            }
            String newText = translation.getMessage(
                    "movement.location.accepted",
                    message,
                    new Object[]{location.getName(message.getPlayer().getLanguage())}
            );
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, distance);
            if (!TimeDependentActions.scheduleMove(
                    message.getPlayer().getId(),
                    calendar.getTimeInMillis(),
                    locationId,
                    String.valueOf(distance))
            ) {
                newText = translation.getMessage("movement.location.in-progress", message);
            } else {
                Player player = message.getPlayer();
                player.move();
                playerService.update(player);
            }
            messages.sendMessage(EditResponseMessage.builder().lang(message)
                    .messageId(message)
                    .text(newText)
                    .targetId(message)
                    .applyMarkup(true).build()
            );
        }
    }

    public void sendLocationUpdate(ScheduledAction action) {
        Player player = playerService.findById(action.playerId);
        Location location = locationService.findById(action.target);
        player.setLocation(location);
        player.stop();
        String text = translation.getMessage("movement.location.arrived", player, new Object[]{location.getName(player)});
        log.warn("Sending message");
        if (location.getImageLink() != null) {
            InputStream stream = dataProvider.getObject(location.getImageLink().getLocation());
            messages.sendMessage(ImageResponseMessage.builder().lang(player)
                    .fileName(location.getSystemName())
                    .fileStream(stream)
                    .caption(text)
                    .targetId(player).build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder().lang(player)
                    .text(text).targetId(player).build()
            );
        }
        log.warn("Message sent");
        experienceService.addExperience(
                player,
                Stats.ENDURANCE,
                Integer.parseInt(action.additionalValue) / 10,
                true
        );
        log.warn(player.getStatus());
        playerService.update(player);
    }
}