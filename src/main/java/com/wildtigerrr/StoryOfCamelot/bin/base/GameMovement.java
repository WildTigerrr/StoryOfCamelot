package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.TimeDependentActions;
import com.wildtigerrr.StoryOfCamelot.bin.enums.MainText;
import com.wildtigerrr.StoryOfCamelot.bin.service.ScheduledAction;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationNearServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.AmazonClient;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class GameMovement {

    @Autowired
    private ResponseManager messages;
    @Autowired
    private GameMain gameMain;
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

    public void sendAvailableLocations(Player player) {
        ArrayList<Location> nearLocations = locationNearService.getNearLocations(player.getLocation());
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button;
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        int buttonsCounter = 0;
        for (Location loc : nearLocations) {
            buttonsCounter++;
            if (buttonsCounter > 2) {
                rowList.add(buttonsRow);
                buttonsRow = new ArrayList<>();
                buttonsCounter = 1;
            }
            button = new InlineKeyboardButton();
            button.setText(loc.getName());
            button.setCallbackData("/move " + loc.getId());
            buttonsRow.add(button);
        }
        rowList.add(buttonsRow);
        keyboard.setKeyboard(rowList);
        messages.sendMessage("Итак, куда пойдём?", keyboard, player.getExternalId());
    }

    public void moveToLocation(UpdateWrapper message, String locationId) {
        Location location = locationService.findById(Integer.parseInt(locationId));
        if (location != null) {
            int distance = locationNearService.getDistance(message.getPlayer().getLocation(), location);
            if (distance == -1) {
                messages.sendMessage(MainText.NO_DIRECT.text(), message.getUserId());
                return;
            }
            String newText = "Ну, пойдем к " + location.getName();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, distance);
            if (!TimeDependentActions.scheduleMove(message.getPlayer().getId(), calendar.getTimeInMillis(), locationId, String.valueOf(distance))) {
                newText = MainText.ALREADY_MOVING.text();
            }
            messages.sendMessageEdit(message.getMessageId(), newText, message.getUserId(), true);
        }
    }

    public void sendLocationUpdate(ScheduledAction action) {
        Player player = playerService.findById(action.playerId);
        Location location = locationService.findById(Integer.valueOf(action.target));
        player.setLocation(location);
        gameMain.addExperience(player, Stats.ENDURANCE, Integer.valueOf(action.additionalValue) / 10, true);
        playerService.update(player);
        if (location.getImageLink() != null) {
            InputStream stream = amazonClient.getObject(location.getImageLink().getLocation());
            messages.sendImage(location.getName(), stream, player.getExternalId(),  MainText.LOCATION_HELLO.text(location.getName()));
        } else {
            messages.sendMessage(location.getName() + ", и что у нас тут?", player.getExternalId());
        }
    }
}