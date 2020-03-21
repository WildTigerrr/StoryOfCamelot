package com.wildtigerrr.StoryOfCamelot.web.service;

public enum CacheType {
    PLAYER_STATE("PlayerState");

    private final String key;

    CacheType(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

}
