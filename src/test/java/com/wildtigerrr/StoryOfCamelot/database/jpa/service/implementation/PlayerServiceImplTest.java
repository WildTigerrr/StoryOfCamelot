package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.PlayerDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Log4j2
class PlayerServiceImplTest extends ServiceBaseTest {

    @Autowired
    private PlayerServiceImpl service;

    @MockBean
    PlayerDao playerDao;

    @Test
    void whenCreateShouldSearchForExistingAndCreateTest() {
        // Given
        String externalId = "test";
        String id = "1";
        Player player = new Player(externalId, "Tiger", new Location(LocationTemplate.TRADING_SQUARE));
        Player mockedPlayer = new Player(externalId, "Tiger", new Location(LocationTemplate.TRADING_SQUARE));
        ReflectionTestUtils.setField(mockedPlayer, "id", id);
        when(playerDao.findByExternalId(externalId)).thenReturn(null);
        when(playerDao.save(player)).thenReturn(mockedPlayer);

        // When
        Player response = service.createIfNotExist(player);

        // Then
        assertEquals(id, response.getId());
        verify(playerDao).findByExternalId(externalId);
        verify(playerDao).save(player);
    }

    @Test
    void whenCreateAndExistShouldReturnExistingTest() {
        // Given
        String externalId = "test";
        String id = "1";
        Player player = new Player(externalId, "Tiger", new Location(LocationTemplate.TRADING_SQUARE));
        Player mockedPlayer = new Player(externalId, "Tiger", new Location(LocationTemplate.TRADING_SQUARE));
        ReflectionTestUtils.setField(mockedPlayer, "id", id);
        when(playerDao.findByExternalId(externalId)).thenReturn(mockedPlayer);

        // When
        Player response = service.createIfNotExist(player);

        // Then
        assertEquals(id, response.getId());
        verify(playerDao).findByExternalId(externalId);
        verify(playerDao, never()).save(player);
    }

    @Test
    void whenSendTopPlayersShouldFilterTopPlayersTest() {
        // Given
        int playersNumber = 25;
        int topPlayersCount = 7;
        List<Player> playerList = new ArrayList<>();
        Player player;
        int i;
        for (i = 0; i < playersNumber; i++) {
            player = new Player("test" + i, "Tiger" + i, new Location(LocationTemplate.FOREST));
            ReflectionTestUtils.setField(player.stats(), "level", (i + 1));
            playerList.add(player);
        }
        when(playerDao.findAll()).thenReturn(playerList);

        // When
        List<Player> response = service.getTopPlayers(topPlayersCount);

        // Then
        assertEquals(topPlayersCount, response.size());
        assertEquals(Integer.valueOf(i), response.get(0).stats().getLevel());
        assertEquals(Integer.valueOf(i - (topPlayersCount - 1)), response.get(response.size() - 1).stats().getLevel());
    }

    @Test
    void whenSendTopPlayersLessThenShouldReturnAllTest() {
        // Given
        int playersNumber = 5;
        int topPlayersCount = 10;
        List<Player> playerList = new ArrayList<>();
        Player player;
        int i;
        for (i = 0; i < playersNumber; i++) {
            player = new Player("test" + i, "Tiger" + i, new Location(LocationTemplate.FOREST));
            ReflectionTestUtils.setField(player.stats(), "level", (i + 1));
            playerList.add(player);
        }
        when(playerDao.findAll()).thenReturn(playerList);

        // When
        List<Player> response = service.getTopPlayers(topPlayersCount);

        // Then
        assertEquals(playersNumber, response.size());
        assertEquals(Integer.valueOf(i), response.get(0).stats().getLevel());
        assertEquals(Integer.valueOf(1), response.get(response.size() - 1).stats().getLevel());
    }

    @Test
    void whenGetPlayerShouldReturnPlayerTest() {
        // Given
        String externalId = "test";
        Player player = new Player(externalId, "Tiger", new Location(LocationTemplate.TRADING_SQUARE));
        when(playerDao.findByExternalId(externalId)).thenReturn(player);

        // When
        Player response = service.getPlayer(externalId);

        // Then
        assertEquals(externalId, response.getExternalId());
        assertEquals("Tiger", response.getNickname());
        verify(playerDao, never()).save(player);
    }

    @Test
    void whenGetPlayerAndNotExistShouldCreateAndReturnPlayerTest() {
        // Given
        String externalId = "test";
        when(playerDao.findByExternalId(externalId)).thenReturn(null);
//        when(locationService.findByName(GameSettings.DEFAULT_LOCATION.get())).thenReturn(new Location(LocationTemplate.THICKET));
        when(playerDao.save(any(Player.class))).thenAnswer((Answer<Player>) invocationOnMock -> invocationOnMock.getArgument(0));

        // When
        Player response = service.getPlayer(externalId);

        // Then
        assertEquals(externalId, response.getExternalId());
        assertEquals(externalId, response.getNickname());
        verify(playerDao).save(any(Player.class));
    }

    @Test
    void whenGetPlayerInfoShouldHaveValidMarkupInAnyLanguageTest() {
        // Given
        String externalId = "test";
        Player player = new Player(externalId, "Tiger", new Location(LocationTemplate.TRADING_SQUARE));

        for (Language lang : Language.values()) {
            player.setLanguage(lang);
            when(playerDao.findByExternalId(externalId)).thenReturn(player);

            // When
            String response = service.getPlayerInfo("test", lang);

            // Then
            for (char searchCharacter : "_*`".toCharArray()) {
                try {
                    assertEvenCharacters(response, searchCharacter);
                } catch (AssertionFailedError e) {
                    throw new AssertionFailedError("Assertion failed for {" + searchCharacter + "} in " + lang.getName(), e);
                }
            }
        }
    }

    @Test
    void whenSettingNicknameShouldAcceptOnlyValidTest() {

    }

    private void assertEvenCharacters(String searchString, char searchCharacter) {
        long count = searchString.chars().filter(ch -> ch == searchCharacter).count();
        assertIsEven(count);
    }

    private void assertIsEven(long value) {
        assertEquals(0, value % 2);
    }

}