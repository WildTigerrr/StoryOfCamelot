package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookInit {

    @Autowired
    private WebHookHandler handler;

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public void webhook(@RequestBody Update update) {
        try {
            handler.onWebhookUpdateReceived(update);
        } catch (Exception e) {
            onError(update, e);
        }
    }

    private void onError(Update update, Exception e) {
        try {
            ResponseManager.sendErrorReport(
                    "Exception during runtime: `" + e.getMessage() + "`; " +
                            "\n\nDuring working on message: `" + new UpdateWrapper(update, update.hasCallbackQuery()) + "`",
                    e, true
            );
        } catch (Exception e1) {
            ResponseManager.sendErrorReport(
                    "Exception on creating UpdateWrapper in exception handle: `" + StringUtils.escape(e1.getMessage()) + "`",
                    e1, true
            );
        }
    }

}