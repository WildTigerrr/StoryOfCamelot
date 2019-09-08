package com.wildtigerrr.StoryOfCamelot.bin.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private static ScheduledExecutorService scheduledExecutorService;

    public static ScheduledFuture<?> schedule(Runnable method) {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }
        return scheduledExecutorService.scheduleAtFixedRate(
                method, 5, 5, TimeUnit.SECONDS);
    }

    public static void cancel(ScheduledFuture<?> task) {
        task.cancel(true);
    }

    public static boolean isActive(ScheduledFuture<?> task) {
        return (task != null && !task.isCancelled());
    }

}
