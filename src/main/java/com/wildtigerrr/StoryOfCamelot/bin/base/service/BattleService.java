package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.base.BattleLog;
import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.BackpackItem;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.MobDrop;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.BackpackService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.MobDropService;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.MobService;
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
    private final MobDropService dropService;
    private final BackpackService backpackService;
    private final DropCalculator dropCalculator;

    @Autowired
    public BattleService(ResponseManager messages, TranslationManager translation, MobService mobService, BattleHandler battleHandler, CacheProvider cacheProvider, MobDropService dropService, BackpackService backpackService, DropCalculator dropCalculator) {
        this.messages = messages;
        this.translation = translation;
        this.mobService = mobService;
        this.battleHandler = battleHandler;
        this.cacheService = cacheProvider;
        this.dropService = dropService;
        this.backpackService = backpackService;
        this.dropCalculator = dropCalculator;
    }

    public BattleLog fight(UpdateWrapper update) {
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("battle.start", update)).targetId(update).build()
        );
        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, update.getPlayer().getId());
        Mob mob = mobService.findById(state.getEnemy().getId());

        BattleLog battleLog = battleHandler.fight(update.getPlayer(), mob, update.getPlayer().getLanguage());
        messages.sendMessage(TextResponseMessage.builder()
                .text(battleLog.getBattleHistory()).targetId(update).build()
        );

        state.setLastBattle(battleLog);
        cacheService.add(CacheType.PLAYER_STATE, state.getId(), state);

        if (battleLog.isWin() && battleLog.getEnemyType() == EnemyType.MOB) applyDrop(battleLog);

        return battleLog;

        // TODO Allow actions by statuses (class to compare)
    }

    private void applyDrop(BattleLog battleLog) {
        List<MobDrop> dropMap = dropService.findByMobId(battleLog.getEnemyId());
        if (dropMap == null || dropMap.isEmpty()) return;
        Backpack backpack = backpackService.findMainByPlayerId(battleLog.getAttackerId());
        List<BackpackItem> newItems = dropCalculator.calculate(dropMap);
        if (newItems.isEmpty()) return;
        StringBuilder builder = new StringBuilder();
        builder.append("Добыча:\n\n");
        backpack.getItems().forEach(item -> builder.append(item.backpackInfo(translation)));
        messages.sendMessage(TextResponseMessage.builder()
                .targetId(backpack.getPlayer())
                .text(builder.toString()).build()
        );
        backpack.put(newItems);
        backpackService.update(backpack);
    }

}
