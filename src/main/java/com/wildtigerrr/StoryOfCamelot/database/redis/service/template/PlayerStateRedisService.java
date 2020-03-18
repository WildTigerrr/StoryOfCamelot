package com.wildtigerrr.StoryOfCamelot.database.redis.service.template;

import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;

import java.util.Map;

public interface PlayerStateRedisService {
    void add(PlayerState state);
    void delete(Integer playerId);
    PlayerState findPlayerState(Integer playerId);
    Map<Object, Object> findAllPlayerStats();
}
