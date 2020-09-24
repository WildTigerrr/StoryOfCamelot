package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.ActionHandler;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.RandomDistribution;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationPossible;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.LocationPossibleService;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FightCommandHandler extends TextMessageHandler {

    private final LocationPossibleService locationPossibleService;
    private final CacheProvider cacheService;
    private final ActionHandler actionHandler;

    public FightCommandHandler(ResponseManager messages, TranslationManager translation, LocationPossibleService locationPossibleService, CacheProvider cacheService, ActionHandler actionHandler) {
        super(messages, translation);
        this.locationPossibleService = locationPossibleService;
        this.cacheService = cacheService;
        this.actionHandler = actionHandler;
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
            PlayerState playerState = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, message.getPlayer().getId());
            cacheService.add(CacheType.PLAYER_STATE, playerState.setEnemy(enemy));
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .keyboard(KeyboardManager.getReplyByButtons(actionHandler.getAvailableActions(message.getPlayer()), message.getPlayer().getLanguage()))
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
