package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class TimeDependentActions {

    private static Boolean hasWaitingAction = false;

    public void startCheckingActions() {
        hasWaitingAction = true;
        checkForWaitingActivities();
    }

    public void stopCheckingActions() {
        hasWaitingAction = false;
    }

    private static void checkForWaitingActivities() {
        hasWaitingAction = true;
        long lastCall = 0;
        Date date;
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            while (hasWaitingAction) {
                if (System.currentTimeMillis() - lastCall > 20000) {
                    lastCall = System.currentTimeMillis();
                    date = new Date(lastCall);
                    new ResponseHandler().sendMessageToAdmin("Last call was on " + formatter.format(date));
                }
            }
        } catch (Exception e) {
            new ResponseHandler().sendMessageToAdmin(e.getMessage());
            e.printStackTrace();
        }
    }

}
