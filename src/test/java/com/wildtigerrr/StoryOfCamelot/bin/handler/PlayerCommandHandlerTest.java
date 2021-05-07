package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdate;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdateMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class PlayerCommandHandlerTest extends ServiceBaseTest {

    @Autowired
    private PlayerCommandHandler playerCommandHandler;

    @MockBean
    private ResponseManager messages;

    @Test
    void process() {
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/" + Command.ME.name()).build()).build().get()
        );
        Player player = new Player("testId", "Nickname", new Location(LocationTemplate.TRADING_SQUARE));
        player.setLanguage(Language.ENG);
        player.setCurrentHealth(15.0);
        message.setPlayer(player);

        playerCommandHandler.process(message);

        verify(messages).sendMessage(any());
    }

}