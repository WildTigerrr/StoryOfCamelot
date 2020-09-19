package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.enums.RandomDistribution;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationPossible;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.LocationPossibleService;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FightCommandHandler extends TextMessageHandler {

    private final LocationPossibleService locationPossibleService;

    public FightCommandHandler(ResponseManager messages, TranslationManager translation, LocationPossibleService locationPossibleService) {
        super(messages, translation);
        this.locationPossibleService = locationPossibleService;
    }

    @Override
    public void process(IncomingMessage message) {
        switch (message.getCommand()) {
            case SEARCH_ENEMIES: findEnemy((TextIncomingMessage) message); break;
        }
    }

    private void findEnemy(TextIncomingMessage message) {
        Location playerLocation = message.getPlayer().getLocation();
        List<LocationPossible> locationMobs = locationPossibleService.getPossibleMobs(playerLocation);
        Mob enemy = getRandomMob(locationMobs);
        if (enemy == null) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("Кажется, тут никого нет").build() // TODO
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("Враг: " + enemy.getName(message)).build() // TODO
            );
        }
    }

    private Mob getRandomMob(List<LocationPossible> possibleList) {
        if (possibleList.isEmpty()) return null;
        int total = possibleList.stream().mapToInt(LocationPossible::getFrequency).sum();
        int random = RandomDistribution.DISCRETE.nextInt(total);
        int counter = 0;
        for (LocationPossible possible : possibleList) {
            if (random < counter + possible.getFrequency()) return possible.getMob();
            else counter += possible.getFrequency();
        }
        return null;
    }

}
