package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.redis;

import com.wildtigerrr.StoryOfCamelot.database.schema.redis.RedisUser;
import org.springframework.data.repository.CrudRepository;

public interface RedisUserDao extends CrudRepository<RedisUser, String> {
}
