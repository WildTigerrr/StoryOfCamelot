package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.database.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.MobServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.template.MobService;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BattleService {

    private final ResponseManager messages;
    private final TranslationManager translation;
    private final MobService mobService;
    private final BattleHandler battleHandler;
    private final CacheProvider cacheService;

    @Autowired
    public BattleService(ResponseManager messages, TranslationManager translation, MobService mobService, BattleHandler battleHandler, CacheProvider cacheProvider) {
        this.messages = messages;
        this.translation = translation;
        this.mobService = mobService;
        this.battleHandler = battleHandler;
        this.cacheService = cacheProvider;
    }

    public void fight(UpdateWrapper update) {
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("battle.start", update)).targetId(update).build()
        );
        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, update.getPlayer().getId());
        Mob mob = mobService.findById(state.getEnemy().getId());

        List<String> battleLog = battleHandler.fight(update.getPlayer(), mob, update.getPlayer().getLanguage());
        StringBuilder history = new StringBuilder();
        for (String logRow : battleLog) {
            history.append(logRow).append("\n");
        }
        messages.sendMessage(TextResponseMessage.builder()
                .text(history.toString()).targetId(update).build()
        );

        // TODO Allow actions by statuses (class to compare)
        // TODO New status (new table?) with current situation
    }

}
