package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.BattleLog;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.ActionHandler;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.BattleHandler;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.DropCalculator;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.*;
import com.wildtigerrr.StoryOfCamelot.bin.service.ListUtils;
import com.wildtigerrr.StoryOfCamelot.bin.service.PlayerAction;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.*;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.*;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.EnemyState;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class FightCommandHandler extends TextMessageHandler {

    private final LocationPossibleService locationPossibleService;
    private final CacheProvider cacheService;
    private final ActionHandler actionHandler;
    private final MobService mobService;
    private final PlayerService playerService;
    private final BattleHandler battleHandler;
    private final MobDropService dropService;
    private final BackpackService backpackService;
    private final DropCalculator dropCalculator;

    public FightCommandHandler(ResponseManager messages, TranslationManager translation, LocationPossibleService locationPossibleService, CacheProvider cacheService, ActionHandler actionHandler, MobService mobService, PlayerService playerService, BattleHandler battleHandler, MobDropService dropService, BackpackService backpackService, DropCalculator dropCalculator) {
        super(messages, translation);
        this.locationPossibleService = locationPossibleService;
        this.cacheService = cacheService;
        this.actionHandler = actionHandler;
        this.mobService = mobService;
        this.playerService = playerService;
        this.battleHandler = battleHandler;
        this.dropService = dropService;
        this.backpackService = backpackService;
        this.dropCalculator = dropCalculator;
    }

    @Override
    public void process(IncomingMessage message) {
        switch (message.getCommand()) {
            case SEARCH_ENEMIES: findEnemy((TextIncomingMessage) message); break;
//            case FIGHT: fight((TextIncomingMessage) message); break;
            case FIGHT: proceedFight((TextIncomingMessage) message); break;
        }
    }

    private void proceedFight(TextIncomingMessage message) {
        ReplyButton button = ReplyButton.getButton(message.text(), message.getPlayer().getLanguage());
        if (button.hasSkill()) fightAction(message, button.getSkill());
        else fightDynamic(message);
    }

    private void findEnemy(TextIncomingMessage message) {
        Location playerLocation = message.getPlayer().getLocation();
        List<LocationPossible> locationMobs = locationPossibleService.getPossibleMobs(playerLocation);
        Mob enemy = getRandomMob(locationMobs);
        if (enemy == null) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(translation.getMessage("battle.no-enemy-here", message)).build()
            );
        } else {
            PlayerState playerState = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, message.getPlayer().getId());
            cacheService.add(CacheType.PLAYER_STATE, playerState.setEnemy(enemy));
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .keyboard(actionHandler.getAvailableActionsKeyboard(message.getPlayer()))
                    .text(translation.getMessage("battle.enemy", message) + enemy.getName(message)).build()
            );
        }
    }

    private void fightDynamic(TextIncomingMessage message) {
        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, message.getPlayer().getId());
        Mob mob = getValidatedEnemy(message, state);
        if (mob == null) return;

        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(translation.getMessage("battle.start", message)).build()
        );
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(translation.getMessage("battle.choose-next-action", message))
                .keyboard(KeyboardManager.getReplyByButtons(actionHandler.getAvailableFightingActions(message.getPlayer()), message.getPlayer().getLanguage()))
                .build()
        );

        state.setEnemyState(EnemyState.of(mob));
        state.initBattleLog(translation.getMessage("battle.log.fight", message.getPlayer().getLanguage()));
        cacheService.add(CacheType.PLAYER_STATE, state.getId(), state);
    }

    private void fightAction(TextIncomingMessage message, Skill skill) {
        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, message.getPlayer().getId());
        Mob mob = getValidatedEnemy(message, state);
        if (mob == null) return;

        BattleLog battleLog = battleHandler.fightDynamic(message.getPlayer(), mob, message.getPlayer().getLanguage(), state.getLastBattle(), skill);
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(battleLog.getBattleHistory()).build()
        );

        state.setLastBattle(battleLog);
        state.getEnemyState().setHitpoints(mob.getHitpoints());

        if (battleLog.isWin() && battleLog.getEnemyType() == EnemyType.MOB) {
            applyDrop(battleLog);
            state.finishBattle();
            message.addAction(PlayerActionType.KILLED, state.getEnemy().getId());
            cacheService.add(CacheType.PLAYER_STATE, state.getId(), state);
            actionHandler.sendAvailableActions(message.getPlayer());
        } else if (battleLog.getBattleFinished()) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(translation.getMessage("battle.death", message)).build()
            );
            message.addAction(PlayerActionType.DEATH, state.getEnemy().getId());
            cacheService.add(CacheType.PLAYER_STATE, state.getId(), state);
        } else {
            cacheService.add(CacheType.PLAYER_STATE, state.getId(), state);
        }
        playerService.update(message.getPlayer());
    }

    private Mob getValidatedEnemy(IncomingMessage message, PlayerState state) {
        if (!state.hasEnemy()) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(translation.getMessage("battle.no-enemy", message)).build()
            );
            return null;
        }
        Mob mob = mobService.findById(state.getEnemy().getId());
        if (mob == null) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(translation.getMessage("battle.no-enemy", message)).build()
            );
            return null;
        }
        if (state.getEnemyState() != null) {
            mob.setHitpoints(state.getEnemyState().getHitpoints());
        }
        return mob;
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

    private void applyDrop(BattleLog battleLog) {
        List<MobDrop> dropMap = dropService.findByMobId(battleLog.getEnemyId());
        if (ListUtils.isEmpty(dropMap)) return;
        Backpack backpack = backpackService.findMainByPlayerId(battleLog.getAttackerId());
        List<BackpackItem> newItems = dropCalculator.calculate(dropMap);
        if (ListUtils.isEmpty(newItems)) return;
        StringBuilder builder = new StringBuilder();
        builder.append(translation.getMessage("battle.loot", backpack.getPlayer()));
        newItems.forEach(item -> builder.append(item.backpackInfo(translation, backpack.getPlayer().getLanguage())));
        messages.sendMessage(TextResponseMessage.builder().by(backpack.getPlayer())
                .text(builder.toString()).build()
        );
        backpack.put(newItems);
        backpackService.update(backpack);
    }

}
