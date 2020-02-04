package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.player.ExperienceService;
import com.wildtigerrr.StoryOfCamelot.bin.service.ScheduledAction;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationNearServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.DataProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.EditResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.ImageResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

@Service
public class GameMovement {

    private ResponseManager messages;
    private ExperienceService experienceService;
    private GameTutorial tutorial;
    private LocationServiceImpl locationService;
    private LocationNearServiceImpl locationNearService;
    private PlayerServiceImpl playerService;
    private DataProvider dataProvider;
    private TranslationManager translation;

    public GameMovement() {
    }

    @Autowired
    GameMovement(
            DataProvider dataProvider,
            TranslationManager translation,
            PlayerServiceImpl playerService,
            LocationNearServiceImpl locationNearService,
            LocationServiceImpl locationService,
            GameTutorial tutorial,
            ExperienceService experienceService,
            ResponseManager messages
    ) {
        this.dataProvider = dataProvider;
        this.translation = translation;
        this.playerService = playerService;
        this.locationNearService = locationNearService;
        this.locationService = locationService;
        this.tutorial = tutorial;
        this.experienceService = experienceService;
        this.messages = messages;
    }

    public void handleMove(UpdateWrapper message) {
        if (message.getPlayer().getStatus() == PlayerStatus.MOVEMENT) {
            if (message.isQuery()) {
                messages.sendMessage(EditResponseMessage.builder()
                        .messageId(message)
                        .text(translation.getMessage("movement.location.in-progress", message))
                        .targetId(message)
                        .applyMarkup(true).build()
                );
            } else {
                messages.sendMessage(TextResponseMessage.builder()
                        .text(translation.getMessage("movement.location.in-progress", message)).targetId(message).build()
                );
            }
        } else if (message.isQuery()) {
            String[] commandParts = message.getText().split(" ", 2);
            moveToLocation(message, commandParts[1]);
        } else { // if (commandParts.length < 2)
            sendAvailableLocations(message.getPlayer());
        }
    }

    public void sendAvailableLocations(Player player) {
        ArrayList<Location> nearLocations = locationNearService.getNearLocations(player.getLocation());
        if (!nearLocations.isEmpty()) {
            messages.sendMessage(TextResponseMessage.builder()
                            .text(translation.getMessage("movement.location.select", player))
                            .keyboard(KeyboardManager.getKeyboardForLocations(nearLocations, player.getLanguage()))
                            .targetId(player).build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(translation.getMessage("movement.location.blocked", player)).targetId(player).build()
            );
        }
    }

    public void moveToLocation(UpdateWrapper message, String locationId) {
        Location location = locationService.findById(Integer.parseInt(locationId));
        if (location != null) {
            int distance = locationNearService.getDistance(
                    message.getPlayer().getLocation(),
                    location
            );
            if (distance == -1) {
                messages.sendMessage(TextResponseMessage.builder()
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
            messages.sendMessage(EditResponseMessage.builder()
                    .messageId(message)
                    .text(newText)
                    .targetId(message)
                    .applyMarkup(true).build()
            );
        }
    }

    public void sendLocationUpdate(ScheduledAction action) {
        Player player = playerService.findById(action.playerId);
        Location location = locationService.findById(Integer.parseInt(action.target));
        player.setLocation(location);
        player.stop();
        if (location.getImageLink() != null) {
            InputStream stream = dataProvider.getObject(location.getImageLink().getLocation());
            messages.sendMessage(ImageResponseMessage.builder()
                    .fileName(location.getSystemName())
                    .fileStream(stream)
                    .caption(translation.getMessage(
                            "movement.location.arrived",
                            player,
                            new Object[]{location.getName(player.getLanguage())}))
                    .targetId(player).build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(translation.getMessage(
                            "movement.location.arrived",
                            player,
                            new Object[]{location.getName(player.getLanguage())}
                    )).targetId(player).build()
            );
        }
        if (player.getAdditionalStatus() == PlayerStatusExtended.TUTORIAL_MOVEMENT) {
            tutorial.tutorialMovement(player);
            return;
        }
        experienceService.addExperience(
                player,
                Stats.ENDURANCE,
                Integer.parseInt(action.additionalValue) / 10,
                true
        );
        playerService.update(player);
    }
}