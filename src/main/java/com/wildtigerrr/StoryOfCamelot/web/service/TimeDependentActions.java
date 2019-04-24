package com.wildtigerrr.StoryOfCamelot.web.service;

import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class TimeDependentActions {

    private static Integer counter = 0;

    public void addCount() {
        counter++;
        new ResponseHandler().sendMessageToAdmin("Updated to: " + counter);
    }


}
