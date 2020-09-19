package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
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
        List<Mob> locationMobs = locationPossibleService.getPossibleMobs(playerLocation);
        if (locationMobs.isEmpty()) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("Кажется, тут никого нет").build() // TODO
            );
        } else {
            StringBuilder mobs = new StringBuilder();
            mobs.append("Здесь у нас: ");
            for (Mob mob : locationMobs) {
                mobs.append(mob.getName(message.getPlayer()));
            }
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(mobs.toString()).build() // TODO
            );
        }
    }

}
