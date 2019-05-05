package com.wildtigerrr.StoryOfCamelot.database.schema.redis;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("RedisUser")
public class RedisUser implements Serializable {

    public enum Status {
        ACTIVE, BANNED
    }

    private String id;
    private Status status;

    public RedisUser(String id) {
        this.id = id;
        this.status = Status.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
