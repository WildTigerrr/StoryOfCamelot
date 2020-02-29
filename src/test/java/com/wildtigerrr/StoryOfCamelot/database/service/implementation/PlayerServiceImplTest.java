package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.PlayerDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerServiceImplTest extends ServiceBaseTest {

    @Autowired
    private PlayerServiceImpl service;

    @MockBean
    PlayerDao playerDao;

    @Test
    void whenCreateShouldSearchForExistingAndCreate() {
        // Given
        String externalId = "test";
        Integer id = 1;
        Player player = new Player(externalId, "Tiger", new Location(LocationTemplate.TRADING_SQUARE));
        Player mockedPlayer = new Player(externalId, "Tiger", new Location(LocationTemplate.TRADING_SQUARE));
        ReflectionTestUtils.setField(mockedPlayer, "id", id);
        when(playerDao.findByExternalId(externalId)).thenReturn(null);
        when(playerDao.save(player)).thenReturn(mockedPlayer);

        // When
        Player response = service.create(player);

        // Then
        assertEquals(id, response.getId());
        verify(playerDao).findByExternalId(externalId);
        verify(playerDao).save(player);
    }

    @Test
    void whenCreateAndExistShouldReturnExisting() {
        // Given
        String externalId = "test";
        Integer id = 1;
        Player player = new Player(externalId, "Tiger", new Location(LocationTemplate.TRADING_SQUARE));
        Player mockedPlayer = new Player(externalId, "Tiger", new Location(LocationTemplate.TRADING_SQUARE));
        ReflectionTestUtils.setField(mockedPlayer, "id", id);
        when(playerDao.findByExternalId(externalId)).thenReturn(mockedPlayer);

        // When
        Player response = service.create(player);

        // Then
        assertEquals(id, response.getId());
        verify(playerDao).findByExternalId(externalId);
        verify(playerDao, never()).save(player);
    }

    @Test
    void whenSendTopPlayersShouldFilterTopPlayers() {
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
    void whenSendTopPlayersLessThenPlayersShouldReturnLess() {
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

}