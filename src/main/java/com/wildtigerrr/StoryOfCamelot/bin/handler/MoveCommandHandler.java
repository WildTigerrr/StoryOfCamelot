package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.ActionHandler;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.player.ExperienceService;
import com.wildtigerrr.StoryOfCamelot.bin.service.ScheduledAction;
import com.wildtigerrr.StoryOfCamelot.bin.service.Time;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.CharacterStatus;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.LocationNearServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import com.wildtigerrr.StoryOfCamelot.web.service.DataProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.EditResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.ImageResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;

@Service
public class MoveCommandHandler extends TextMessageHandler {

    private final DataProvider dataProvider;

    private final CacheProvider cacheService;
    private final PlayerService playerService;
    private final ExperienceService experienceService;
    private final LocationServiceImpl locationService;
    private final LocationNearServiceImpl locationNearService;
    private final ActionHandler actionHandler;

    public MoveCommandHandler(ResponseManager messages, TranslationManager translation, DataProvider dataProvider, CacheProvider cacheService, PlayerService playerService, ExperienceService experienceService, LocationServiceImpl locationService, LocationNearServiceImpl locationNearService, ActionHandler actionHandler) {
        super(messages, translation);
        this.dataProvider = dataProvider;
        this.cacheService = cacheService;
        this.playerService = playerService;
        this.experienceService = experienceService;
        this.locationService = locationService;
        this.locationNearService = locationNearService;
        this.actionHandler = actionHandler;
    }

    @Override
    public void process(IncomingMessage message) {
        handleMove(message);
    }

    public void handleMove(IncomingMessage message) {
        PlayerState playerState = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, message.getPlayer().getId());
        if (playerState.isMoving()) {
            if (message.isQuery()) {
                messages.sendMessage(EditResponseMessage.builder().by(message)
                        .text(translation.getMessage("movement.location.in-progress", message))
                        .applyMarkup(true).build()
                );
            } else {
                messages.sendMessage(TextResponseMessage.builder().by(message)
                        .text(translation.getMessage("movement.location.in-progress", message)).build()
                );
            }
        } else if (message.isQuery()) {
            moveToLocation(message, playerState, ((TextIncomingMessage) message).getParsedCommand().paramByNum(1));
        } else {
            sendAvailableLocations(message.getPlayer());
        }
    }

    public void sendAvailableLocations(Player player) {
        ArrayList<Location> nearLocations = locationNearService.getNearLocations(player.getLocation());
        if (!nearLocations.isEmpty()) {
            messages.sendMessage(TextResponseMessage.builder().by(player)
                    .text(translation.getMessage("movement.location.select", player))
                    .keyboard(KeyboardManager.getKeyboardForLocations(nearLocations, player.getLanguage())).build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder().by(player)
                    .text(translation.getMessage("movement.location.blocked", player)).build()
            );
        }
    }

    public void moveToLocation(IncomingMessage message, PlayerState playerState, String locationId) {
        Location location = locationService.findById(locationId);
        if (location == null) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(translation.getMessage("movement.location.unknown-location", message)).build()
            );
            return;
        }
        int distance = locationNearService.getDistance(
                message.getPlayer().getLocation(),
                location
        );
        if (distance == -1) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(translation.getMessage("movement.location.no-connection", message)).build()
            );
            return;
        }
        String newText = translation.getMessage(
                "movement.location.accepted",
                message,
                new Object[]{location.getName(message.getPlayer().getLanguage())}
        );
        if (!TimeDependentActions.scheduleMove(
                message.getPlayer().getId(),
                Time.getTimeAfter(Time.seconds(distance)),
                locationId,
                String.valueOf(distance))
        ) {
            newText = translation.getMessage("movement.location.in-progress", message);
        } else {
            cacheService.add(CacheType.PLAYER_STATE, playerState.move());
        }
        messages.sendMessage(EditResponseMessage.builder().lang(message)
                .messageId(message)
                .text(newText)
                .targetId(message)
                .applyMarkup(true).build()
        );
    }

    public void sendLocationUpdate(ScheduledAction action) {
        try {
            updateLocation(action);
        } catch (Exception e) {
            handleMovementError(action, e);
        }
    }

    private void updateLocation(ScheduledAction action) {
        Player player = playerService.findById(action.playerId);
        PlayerState playerState = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, player.getId());
        Location location = locationService.findById(action.target);
        player.setLocation(location);
        cacheService.add(CacheType.PLAYER_STATE, playerState.stop());
        String text = translation.getMessage("movement.location.arrived", player, new Object[]{location.getName(player)});
        experienceService.addExperience(
                player,
                Stats.ENDURANCE,
                Integer.parseInt(action.additionalValue) / 10,
                true
        );
        playerService.update(player);
        if (location.getImageLink() != null) {
            InputStream stream = dataProvider.getObject(location.getImageLink().getLocation());
            messages.sendMessage(ImageResponseMessage.builder().by(player)
                    .fileName(location.getSystemName()).fileStream(stream)
                    .caption(text).build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder().by(player)
                    .keyboard(actionHandler.getAvailableActionsKeyboard(player))
                    .text(text).build()
            );
        }
    }

    private void handleMovementError(ScheduledAction action, Exception e) {
        messages.sendErrorReport(action.toString(), e);
    }

}
