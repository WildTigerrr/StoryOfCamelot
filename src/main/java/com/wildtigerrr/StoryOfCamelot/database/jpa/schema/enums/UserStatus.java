package com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums;

public enum UserStatus {
    ACTIVE,
    MUTED_MINUTES,
    MUTED_HOUR,
    MUTED_HOURS,
    MUTED_DAYS,
    BANNED;

    private UserStatus onBan;

    static {
        ACTIVE.onBan = MUTED_MINUTES;
        MUTED_MINUTES.onBan = MUTED_HOUR;
        MUTED_HOURS.onBan = MUTED_DAYS;
        MUTED_DAYS.onBan = BANNED;
        BANNED.onBan = BANNED;
    }

    public UserStatus ifBan() {
        return onBan;
    }

}
