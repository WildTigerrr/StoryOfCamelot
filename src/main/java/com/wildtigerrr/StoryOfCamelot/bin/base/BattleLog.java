package com.wildtigerrr.StoryOfCamelot.bin.base;

import com.wildtigerrr.StoryOfCamelot.bin.enums.EnemyType;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class BattleLog implements Serializable {

    private final String attackerId;
    private final String enemyId;
    private final EnemyType enemyType;
    
    private final Boolean attackerWins;
    private final transient List<String> log;

    public BattleLog(String attackerId, String enemyId, EnemyType enemyType, Boolean attackerWins, List<String> log) {
        this.attackerId = attackerId;
        this.enemyId = enemyId;
        this.enemyType = enemyType;
        this.attackerWins = attackerWins;
        this.log = log;
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
