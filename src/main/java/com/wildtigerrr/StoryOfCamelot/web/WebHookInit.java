package com.wildtigerrr.StoryOfCamelot.web;

import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j2
@RestController
public class WebHookInit {

    private final WebHookHandler handler;
    private final ResponseManager messages;

    @Autowired
    public WebHookInit(WebHookHandler handler, ResponseManager messages) {
        this.handler = handler;
        this.messages = messages;
    }

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
            sendErrorReport(
                    "Exception during runtime: `" + e.getMessage() + "`; " +
                    "\n\nDuring working on message: `" + new UpdateWrapper(update) + "`", e);
        } catch (Exception e1) {
            sendErrorReport("Exception on creating UpdateWrapper in exception handle: `" + StringUtils.escape(e1.getMessage()) + "`", e);
        }
    }

    private void sendErrorReport(String message, Exception e) {
        log.error(message, e);
        messages.postMessageToAdminChannel(message, true);
    }

}