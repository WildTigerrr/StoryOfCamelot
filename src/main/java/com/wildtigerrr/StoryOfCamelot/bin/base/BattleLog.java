package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BattleLog implements Serializable {

    private final String attackerId;
    private final String enemyId;
    private final EnemyType enemyType;
    
    private final Boolean attackerWins;
    private final Boolean battleFinished;
    private final ArrayList<String> log;

    public BattleLog(String attackerId, String enemyId, EnemyType enemyType, Boolean attackerWins, Boolean battleFinished, ArrayList<String> log) {
        this.attackerId = attackerId;
        this.enemyId = enemyId;
        this.enemyType = enemyType;
        this.attackerWins = attackerWins;
        this.battleFinished = battleFinished;
        this.log = log;
    }

    // TODO Refactor
    public BattleLog(BattleLog battleLog, boolean attackerWins, boolean battleFinished) {
        this.attackerId = battleLog.getAttackerId();
        this.enemyId = battleLog.getEnemyId();
        this.enemyType = battleLog.getEnemyType();
        this.attackerWins = attackerWins;
        this.battleFinished = battleFinished;
        this.log = battleLog.getLog();
    }

    public Boolean isWin() {
        return attackerWins;
    }
    
    public String getBattleHistory() {
        StringBuilder history = new StringBuilder();
        for (String logRow : log) {
            history.append(logRow).append("\n");
        }
        return history.toString();
    }
    
}
