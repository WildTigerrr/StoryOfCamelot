package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.MobTemplate;
import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.LocationDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.database.redis.schema.PlayerState;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheProvider;
import com.wildtigerrr.StoryOfCamelot.web.service.CacheType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Service
public class TestFactory {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private CacheProvider cacheService;

    private static int recordsCount = 0;

    private int nextId() {
        return recordsCount++;
    }

    public Player createPlayer() {
        int id = nextId();
        Player player = new Player("testId" + id, "Nickname" + id, createLocation(LocationTemplate.TRADING_SQUARE));
        player.setLanguage(Language.ENG);
        return playerService.createIfNotExist(player);
    }

    public Player createPlayer(boolean initCache) {
        Player player = createPlayer();
        if (initCache) initCache(player);
        return player;
    }

    public Location createLocation(LocationTemplate template) {
        Location initial = new Location(template);
        return locationDao.save(initial);
    }

    public void initCache(Player player) {
        cacheService.add(CacheType.PLAYER_STATE, new PlayerState(player.getId()));
    }

    public static NameTranslation createNameTranslation(String name) {
        NameTranslation nameTranslation = mock(NameTranslation.class);
        when(nameTranslation.getName((Language) any())).thenReturn(name);
        return nameTranslation;
    }

    public static NameTranslation createNameTranslation() {
        return createNameTranslation("Test Name");
    }

    public static Mob createMob(String name) {
        NameTranslation nameTranslation = createNameTranslation(name);

        MobTemplate mobTemplate = mock(MobTemplate.class);
        when(mobTemplate.name()).thenReturn(name);
        when(mobTemplate.getName()).thenReturn(nameTranslation);
        when(mobTemplate.getLevel()).thenReturn(1);
        when(mobTemplate.getDamage()).thenReturn(1);
        when(mobTemplate.getHitpoints()).thenReturn(1);
        when(mobTemplate.getDefence()).thenReturn(1);
        when(mobTemplate.getAgility()).thenReturn(1);
        when(mobTemplate.getFileLink()).thenReturn(null);

        return new Mob(mobTemplate);
    }

    public static Mob createMob() {
        return createMob("Test Mob");
    }

    public static Location createLocationMock(String name) {
        NameTranslation nameTranslation = createNameTranslation(name);

        LocationTemplate locationTemplate = mock(LocationTemplate.class);
        when(locationTemplate.name()).thenReturn(name);
        when(locationTemplate.getTranslations()).thenReturn(nameTranslation);
        when(locationTemplate.getFileLink()).thenReturn(null);
        when(locationTemplate.hasStores()).thenReturn(false);
        when(locationTemplate.hasEnemies()).thenReturn(false);

        return new Location(locationTemplate);
    }

    public static Location createLocationMock() {
        return createLocationMock("Test Location");
    }

}
