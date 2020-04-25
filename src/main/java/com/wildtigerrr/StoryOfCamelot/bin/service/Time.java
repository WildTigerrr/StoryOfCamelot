package com.wildtigerrr.StoryOfCamelot.bin.service;

import lombok.experimental.UtilityClass;

import java.util.Calendar;

@UtilityClass
public class Time {
    
    private final long MILLISECONDS_IN_SECOND = 1000;
    private final long SECONDS_IN_MINUTE = 60;
    private final long MINUTES_IN_HOUR = 60;
    private final long HOURS_IN_DAY = 24;

    public long days(long number) {
        return hours(number) * HOURS_IN_DAY;
    }

    public long hours(long number) {
        return minutes(number) * MINUTES_IN_HOUR;
    }

    public long minutes(long number) {
        return seconds(number) * SECONDS_IN_MINUTE;
    }
    
    public long seconds(long number) {
        return number * MILLISECONDS_IN_SECOND;
    }

    public long getTimeAfter(long milliseconds) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.SECOND, (int) seconds(milliseconds));
//        return calendar.getTimeInMillis();
        return System.currentTimeMillis() + milliseconds;
    }
    
}
