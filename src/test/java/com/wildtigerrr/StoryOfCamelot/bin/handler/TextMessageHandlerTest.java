package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.LocationDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdate;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdateMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class TextMessageHandlerTest extends ServiceBaseTest {

    @Autowired
    private TextMessageHandler textMessageHandler;

    @MockBean
    private StartCommandHandler startCommandHandler;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private LocationDao locationDao;

    @Captor
    ArgumentCaptor<IncomingMessage> messageArguments;

    @Test
    void whenMoveAlreadyScheduledShouldInformPlayerTest() {
        // Given
        Location initial = new Location(LocationTemplate.TRADING_SQUARE);
        locationDao.save(initial);

        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/rattata").build()).build().get()
        );

        Player player = new Player("testId", "Nickname", initial);
        player.setLanguage(Language.ENG);
        player = playerService.createIfNotExist(player);
        message.setPlayer(player);

        // When
        textMessageHandler.process(message);

        // Then
        verify(startCommandHandler).process(messageArguments.capture());
    }

}