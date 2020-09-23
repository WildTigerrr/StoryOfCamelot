package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.MobTemplate;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestFactory {

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

    public static Location createLocation(String name) {
        NameTranslation nameTranslation = createNameTranslation(name);

        LocationTemplate locationTemplate = mock(LocationTemplate.class);
        when(locationTemplate.name()).thenReturn(name);
        when(locationTemplate.getTranslations()).thenReturn(nameTranslation);
        when(locationTemplate.getFileLink()).thenReturn(null);
        when(locationTemplate.hasStores()).thenReturn(false);
        when(locationTemplate.hasEnemies()).thenReturn(false);

        return new Location(locationTemplate);
    }

    public static Location createLocation() {
        return createLocation("Test Location");
    }

}
