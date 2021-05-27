package com.wildtigerrr.StoryOfCamelot;

import com.wildtigerrr.StoryOfCamelot.bin.service.ApplicationContextProvider;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;

@SpringBootTest
@ActiveProfiles("test")
public class ServiceBaseTest {

    @BeforeAll
    static void setupTestProperties() throws NoSuchFieldException, IllegalAccessException {
        Field field = ApplicationContextProvider.class.getDeclaredField("IS_TEST");
        field.setAccessible(true);
        field.set(null, true);
    }

}
