package com.wildtigerrr.StoryOfCamelot.web.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Profile("test")
public class MockCacheProvider implements CacheProvider {

    private static Map<String, Map<String, Object>> cache;

    public MockCacheProvider() {
        cache = new HashMap<>();
    }

    @Override
    public void add(CacheType channel, Object key, Object object) {
        createChannelIfNotExist(channel);
        cache.get(channel.key()).put(String.valueOf(key), object);
    }

    @Override
    public void delete(CacheType channel, Object key) {
        createChannelIfNotExist(channel);
        cache.get(channel.key()).remove(String.valueOf(key));
    }

    @Override
    public Object findObject(CacheType channel, Object key) {
        createChannelIfNotExist(channel);
        return cache.get(channel.key()).get(String.valueOf(key));
    }

    @Override
    public Map<String, Object> findAll(CacheType channel) {
        createChannelIfNotExist(channel);
        return cache.get(channel.key());
    }

    @Override
    public void clearCache() {
        for (CacheType channel : CacheType.values()) {
            clearChannel(channel);
        }
    }

    @Override
    public void clearChannel(CacheType channel) {
        cache.get(channel.key()).clear();
    }

    private void createChannelIfNotExist(CacheType channel) {
        if (!cache.containsKey(channel.key())) cache.put(channel.key(), new HashMap<>());
    }

}
