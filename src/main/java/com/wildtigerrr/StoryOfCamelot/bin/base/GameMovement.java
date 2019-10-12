package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

@Service
public class GameMovement {

    private ResponseManager messages;
    private GameMain gameMain;
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
            GameMain gameMain,
            ResponseManager messages
    ) {
        this.dataProvider = dataProvider;
        this.translation = translation;
        this.playerService = playerService;
        this.locationNearService = locationNearService;
        this.locationService = locationService;
        this.tutorial = tutorial;
        this.gameMain = gameMain;
        this.messages = messages;
    }

    public void handleMove(UpdateWrapper message) {
        if (message.getPlayer().getStatus() == PlayerStatus.MOVEMENT) {
            if (message.isQuery()) {
                messages.sendMessageEdit(
                        message.getMessageId(),
//                        MainText.ALREADY_MOVING.text(message.getPlayer().getLanguage()),
                        translation.getMessage("movement.location.in-progress", message),
                        message.getUserId(),
                        true
                );
            } else {
                messages.sendMessage(translation.getMessage("movement.location.in-progress", message), message.getUserId());
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
            messages.sendMessage(
                    translation.getMessage("movement.location.select", player),
                    KeyboardManager.getKeyboardForLocations(nearLocations, player.getLanguage()),
                    player.getExternalId()
            );
        } else {
            messages.sendMessage(
                    translation.getMessage("movement.location.blocked", player),
                    player.getExternalId()
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
                messages.sendMessage(
                        translation.getMessage("movement.location.no-connection", message),
                        message.getUserId()
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
            messages.sendMessageEdit(
                    message.getMessageId(),
                    newText,
                    message.getUserId(),
                    true
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
            messages.sendImage(
                    location.getSystemName(),
                    stream,
                    player.getExternalId(),
                    translation.getMessage(
                            "movement.location.arrived",
                            player,
                            new Object[]{location.getName(player.getLanguage())})
            );
        } else {
            messages.sendMessage(
                    translation.getMessage(
                            "movement.location.arrived",
                            player,
                            new Object[]{location.getName(player.getLanguage())}
                    ),
                    player.getExternalId()
            );
        }
        if (player.getAdditionalStatus() == PlayerStatusExtended.TUTORIAL_MOVEMENT) {
            tutorial.tutorialMovement(player);
            return;
        }
        gameMain.addExperience(
                player,
                Stats.ENDURANCE,
                Integer.parseInt(action.additionalValue) / 10,
                true
        );
        playerService.update(player);
    }
}