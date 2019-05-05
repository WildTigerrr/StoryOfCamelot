package com.wildtigerrr.StoryOfCamelot.database.service.implementation.redis;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.redis.RedisUserDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.redis.RedisUser;
import com.wildtigerrr.StoryOfCamelot.database.service.template.redis.RedisUserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class RedisUserServiceImpl implements RedisUserService {

    @Autowired
    private RedisUserDao redisUserDao;

    @Override
    public RedisUser create(RedisUser user) {
        return redisUserDao.save(user);
    }

    @Override
    public RedisUser get(String id) {
        Optional obj = redisUserDao.findById(id);
        if (obj.isPresent()) {
            return (RedisUser) obj.get();
        }
        return null;
    }

    @Override
    public void delete(String id) {
        redisUserDao.deleteById(id);
    }

    @Override
    public RedisUser update(RedisUser user) {
        return null;
    }
}
