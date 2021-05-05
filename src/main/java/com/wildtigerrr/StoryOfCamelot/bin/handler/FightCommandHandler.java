package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.BattleLog;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.ActionHandler;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.BattleHandler;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.DropCalculator;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import com.wildtigerrr.StoryOfCamelot.bin.enums.RandomDistribution;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Skill;
import com.wildtigerrr.StoryOfCamelot.bin.service.ListUtils;
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
                    .text("Кажется, тут никого нет").build() // TODO
            );
        } else {
            PlayerState playerState = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, message.getPlayer().getId());
            cacheService.add(CacheType.PLAYER_STATE, playerState.setEnemy(enemy));
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .keyboard(actionHandler.getAvailableActionsKeyboard(message.getPlayer()))
                    .text("Враг: " + enemy.getName(message)).build() // TODO
            );
        }
    }

    private void fightDynamic(TextIncomingMessage message) {
        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, message.getPlayer().getId());
        if (!state.hasEnemy()) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("У вас нет противника").build()
            );
            return;
        }

        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(translation.getMessage("battle.start", message)).build()
        );
        Mob mob = mobService.findById(state.getEnemy().getId());

        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text("Выберите следующее действие:")
                .keyboard(KeyboardManager.getReplyByButtons(actionHandler.getAvailableFightingActions(message.getPlayer()), message.getPlayer().getLanguage()))
                .build()
        );

        ArrayList<String> logRows = new ArrayList<>();
        logRows.add(translation.getMessage("battle.log.fight", message.getPlayer().getLanguage()));
        BattleLog battleLog = new BattleLog(
                state.getId(),
                mob.getId(),
                EnemyType.MOB,
                false,
                false,
                logRows
        );

        state.setLastBattle(battleLog);
        state.setEnemyState(EnemyState.of(mob));
        cacheService.add(CacheType.PLAYER_STATE, state.getId(), state);

        message.getPlayer().setCurrentHealth(message.getPlayer().getStats().getHealth());
        playerService.update(message.getPlayer());
    }

    private void fightAction(TextIncomingMessage message, Skill skill) {
        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, message.getPlayer().getId());
        if (!state.hasEnemy()) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("У вас нет противника").build()
            );
            return;
        }

        Mob mob = mobService.findById(state.getEnemy().getId());
        mob.setHitpoints(state.getEnemyState().getHitpoints());

        BattleLog battleLog = battleHandler.fightDynamic(message.getPlayer(), mob, message.getPlayer().getLanguage(), state.getLastBattle(), skill);
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(battleLog.getBattleHistory()).build()
        );

        state.setLastBattle(battleLog);
        state.getEnemyState().setHitpoints(mob.getHitpoints());
        playerService.update(message.getPlayer());

        if (battleLog.isWin() && battleLog.getEnemyType() == EnemyType.MOB) {
            applyDrop(battleLog);
            state.finishBattle();
            actionHandler.sendAvailableActions(message.getPlayer());
        }

        cacheService.add(CacheType.PLAYER_STATE, state.getId(), state);
    }

    private void fight(TextIncomingMessage message) {
        PlayerState state = (PlayerState) cacheService.findObject(CacheType.PLAYER_STATE, message.getPlayer().getId());
        if (!state.hasEnemy()) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("У вас нет противника").build()
            );
            return;
        }

        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(translation.getMessage("battle.start", message)).build()
        );
        Mob mob = mobService.findById(state.getEnemy().getId());

        BattleLog battleLog = battleHandler.fight(message.getPlayer(), mob, message.getPlayer().getLanguage());
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(battleLog.getBattleHistory()).build()
        );

        state.setLastBattle(battleLog);
        cacheService.add(CacheType.PLAYER_STATE, state.getId(), state);

        if (battleLog.isWin() && battleLog.getEnemyType() == EnemyType.MOB) applyDrop(battleLog);
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
        builder.append("Добыча:\n\n");
        newItems.forEach(item -> builder.append(item.backpackInfo(translation, backpack.getPlayer().getLanguage())));
        messages.sendMessage(TextResponseMessage.builder().by(backpack.getPlayer())
                .text(builder.toString()).build()
        );
        backpack.put(newItems);
        backpackService.update(backpack);
    }

}
