package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.web.WebHookHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class SOCBotInitializer {
/* @Component implements CommaLineRunner
    private static final String LOGTAG = "MAIN";

    @Override
    public void run(String... args) throws Exception {
        main(args);
    }

    public static void main(String[] args) {
        // My Logic
        StoryOfCamelotApplication.helloText = "It's working!";
        System.out.println("TEST DEBUG!!!");
//        System.out.println(args);

        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = createTelegramBotsApi();
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    private static TelegramBotsApi createTelegramBotsApi() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(WebHookHandler.getBot());
        return telegramBotsApi;
    }*/

    @Autowired
    private WebHookHandler handler;

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public void webhook(@RequestBody Update update) {
        handler.onWebhookUpdateReceived(update);
    }

}
