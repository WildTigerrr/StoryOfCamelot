package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.web.WebHookHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class SOCBotInitializer {

    private WebHookHandler handler;

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public void webhook(@RequestBody Update update) {
        System.out.println("----------------HANDLER-----------------");
        if (handler == null) handler = new WebHookHandler();
        handler.onWebhookUpdateReceived(update);
    }

}
