package com.wildtigerrr.StoryOfCamelot.database.service.template.redis;

import com.wildtigerrr.StoryOfCamelot.database.schema.redis.RedisUser;

public interface RedisUserService {
    RedisUser create(RedisUser user);
    RedisUser get(String id);
    void delete(String id);
    RedisUser update(RedisUser user);
}
