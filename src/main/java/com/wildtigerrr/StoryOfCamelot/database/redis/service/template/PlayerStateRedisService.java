package com.wildtigerrr.StoryOfCamelot.database.redis.service.template;

import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;

import java.util.Map;

public interface PlayerStateRedisService {
    void add(PlayerState state);
    void delete(String playerId);
    PlayerState findPlayerState(String playerId);
    Map<Object, Object> findAllPlayerStats();
}
