package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.service.ScheduledAction;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatus;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.PlayerStatusExtended;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationNearServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.AmazonClient;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

@Service
public class GameMovement {

    @Autowired
    private ResponseManager messages;
    @Autowired
    private GameMain gameMain;
    @Autowired
    private GameTutorial tutorial;
    @Autowired
    private LocationServiceImpl locationService;
    @Autowired
    private LocationNearServiceImpl locationNearService;
    @Autowired
    private PlayerServiceImpl playerService;
    private AmazonClient amazonClient;

    @SuppressWarnings("unused")
    public GameMovement() {
    }

    @SuppressWarnings("unused")
    @Autowired
    GameMovement(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    public void handleMove(UpdateWrapper message) {
        if (message.getPlayer().getStatus() == PlayerStatus.MOVEMENT) {
            if (message.isQuery()) {
                messages.sendMessageEdit(
                        message.getMessageId(),
                        MainText.ALREADY_MOVING.text(message.getPlayer().getLanguage()),
                        message.getUserId(),
                        true
                );
            } else {
                messages.sendMessage(MainText.ALREADY_MOVING.text(message.getPlayer().getLanguage()), message.getUserId());
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
                    MainText.LOCATION_SELECT.text(player.getLanguage()),
                    KeyboardManager.getKeyboardForLocations(nearLocations, player.getLanguage()),
                    player.getExternalId()
            );
        } else {
            messages.sendMessage(
                    MainText.LOCATION_BLOCKED.text(player.getLanguage()),
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
                        MainText.NO_DIRECT.text(message.getPlayer().getLanguage()),
                        message.getUserId()
                );
                return;
            }
            String newText = MainText.LOCATION_SELECTED.text(message.getPlayer().getLanguage(), location.getName(message.getPlayer().getLanguage()));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, distance);
            if (!TimeDependentActions.scheduleMove(
                            message.getPlayer().getId(),
                            calendar.getTimeInMillis(),
                            locationId,
                            String.valueOf(distance))
            ) {
                newText = MainText.ALREADY_MOVING.text(message.getPlayer().getLanguage());
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
        Location location = locationService.findById(Integer.valueOf(action.target));
        player.setLocation(location);
        player.stop();
        if (location.getImageLink() != null) {
            InputStream stream = amazonClient.getObject(location.getImageLink().getLocation());
            messages.sendImage(
                    location.getSystemName(),
                    stream,
                    player.getExternalId(),
                    MainText.LOCATION_ARRIVED.text(player.getLanguage(), location.getName(player.getLanguage()))
            );
        } else {
            messages.sendMessage(
                    MainText.LOCATION_ARRIVED.text(player.getLanguage(), location.getName(player.getLanguage())),
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
                Integer.valueOf(action.additionalValue) / 10,
                true
        );
        playerService.update(player);
    }
}