package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.TestFactory;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationPossible;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

class FightCommandHandlerTest extends ServiceBaseTest {

    @Autowired
    FightCommandHandler service;

    @Test
    void whenRandomizingEnemyShouldIncludeAllVariantsTest() {
        List<LocationPossible> possibleList = new ArrayList<>();
        Location testLocation = TestFactory.createLocation();
        possibleList.add(new LocationPossible(TestFactory.createMob("First"), testLocation, 2));
        possibleList.add(new LocationPossible(TestFactory.createMob("Second"), testLocation, 1));

        int firstCounter = 0;
        int secondCounter = 0;
        for (int i = 0; i < 100; i++) {
            if (((Mob) ReflectionTestUtils.invokeMethod(service, "getRandomMob", possibleList)).getName(Language.ENG).equals("First")) {
                firstCounter++;
            } else {
                secondCounter++;
            }
        }
        System.out.println(firstCounter);
        System.out.println(secondCounter);
        assertTrue(secondCounter > 0);
        assertTrue(firstCounter > secondCounter);
    }


}