package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.database.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class IdGeneratorTest extends ServiceBaseTest {

//    @Autowired
    private IdGenerator idGenerator;

    @BeforeEach
    void setup() {
        idGenerator = new IdGenerator();
    }


    @Test
    void whenGetEntityTypeShouldReturnNextIdTest() {
        assertEquals("a0p000000000001", idGenerator.generateId(Player.class));
        assertEquals("a0p000000000002", idGenerator.generateId(Player.class));

        assertEquals("a0b000000000001", idGenerator.generateId(Backpack.class));

        assertEquals("a0f000000000001", idGenerator.generateId(FileLink.class));

        assertEquals("a0i000000000001", idGenerator.generateId(Item.class));

        assertEquals("a0l000000000001", idGenerator.generateId(Location.class));

        assertEquals("a0ln00000000001", idGenerator.generateId(LocationNear.class));

        assertEquals("a0m000000000001", idGenerator.generateId(Mob.class));

//        assertEquals("a0md00000000001", idGenerator.generateId(MobDrop.class));

        assertEquals("a0n000000000001", idGenerator.generateId(Npc.class));

        assertEquals("a0pl00000000001", idGenerator.generateId(PossibleLocation.class));
    }

    @Test
    void whenGetWrongTypeShouldThrowExceptionTest() {
        try {
            idGenerator.generateId(PlayerStats.class);
            fail("Should return error on class without Id");
        } catch (Exception ignored) {
        }
        assertTrue(true, "Asserted by exception");
    }

    @Test
    @DirtiesContext
    void whenExceededMaximumShouldThrowExceptionTest() {
        ReflectionTestUtils.setField(idGenerator, "lastIds", new HashMap<String, String>(){{
            put("Player", "a0p0zzzzzzzzzzz");
        }});

        try {
            idGenerator.generateId(Player.class);
            fail("Should return error when exceeded Id");
        } catch (Exception ignored) {
        }
        assertTrue(true, "Asserted by exception");
    }

    @Test
    @DirtiesContext
    void whenExceededSymbolShouldSkipToNextTest() {
        ReflectionTestUtils.setField(idGenerator, "lastIds", new HashMap<String, String>(){{
            put("Player", "a0p000000000009");
        }});

        assertEquals("a0p00000000000a", idGenerator.generateId(Player.class));
    }

}
