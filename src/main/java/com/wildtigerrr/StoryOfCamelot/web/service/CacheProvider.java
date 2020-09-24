package com.wildtigerrr.StoryOfCamelot.web.service;

import java.util.Map;

public interface CacheProvider {

    void add(CacheType channel, Object key, Object object);
    void add(CacheType channel, CacheTypeObject object);
    void delete(CacheType channel, Object key);
    Object findObject(CacheType channel, Object key);
    Map<String, Object> findAll(CacheType channel);

    void clearCache();
    void clearChannel(CacheType channel);

}
