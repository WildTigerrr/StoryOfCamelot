package com.wildtigerrr.StoryOfCamelot.web.service.impl;

import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

@Log4j2
@Repository
@Profile("!test")
public class RedisCacheService implements CacheProvider {

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, Object> hashOperations;
    @Autowired
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }
    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
        clearCache(); // TODO Remove
    }

    @Override
    public void add(CacheType channel, Object key, Object object) {
        hashOperations.put(channel.key(), String.valueOf(key), object);
    }

    @Override
    public void delete(CacheType channel, Object key) {
        hashOperations.delete(channel.key(), String.valueOf(key));
    }

    @Override
    public Object findObject(CacheType channel, Object key) {
        return hashOperations.get(channel.key(), String.valueOf(key));
    }

    @Override
    public Map<String, Object> findAll(CacheType channel) {
        return hashOperations.entries(channel.key());
    }

    @Override
    public void clearCache() {
        for (CacheType channel : CacheType.values()) {
            clearChannel(channel);
        }
    }

    @Override
    public void clearChannel(CacheType channel) {
        Set<String> keys = hashOperations.keys(channel.key());
        log.warn(keys);
        for (String key : keys) {
            log.warn(key);
            delete(channel, key);
        }
    }

}
