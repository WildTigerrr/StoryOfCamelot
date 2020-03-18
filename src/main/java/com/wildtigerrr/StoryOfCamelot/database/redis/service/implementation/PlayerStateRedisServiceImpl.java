package com.wildtigerrr.StoryOfCamelot.database.redis.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.database.redis.service.template.PlayerStateRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

@Repository
public class PlayerStateRedisServiceImpl implements PlayerStateRedisService {

    private static final String KEY = "PlayerState";
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;
    @Autowired
    public PlayerStateRedisServiceImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }
    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }


    @Override
    public void add(PlayerState state) {
        hashOperations.put(KEY, state.getId(), state);
    }

    @Override
    public void delete(Integer playerId) {
        hashOperations.delete(KEY, playerId);
    }

    @Override
    public PlayerState findPlayerState(Integer playerId) {
        return (PlayerState) hashOperations.get(KEY, playerId);
    }

    @Override
    public Map<Object, Object> findAllPlayerStats() {
        return hashOperations.entries(KEY);
    }

}
