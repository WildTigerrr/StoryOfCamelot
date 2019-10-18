package com.wildtigerrr.StoryOfCamelot.bin.translation;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidPropertyException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class TranslationManagerTest extends ServiceBaseTest {

    @Autowired
    private TranslationManager translationManager;

    @MockBean
    private MessageSource messageSourceMock;

    private String message = "Success!";
    private String validCode = "test.success.message";
    private String inValidCode = "test.un-existing.code";

    @Test
    void whenValidCodeShouldReturnMessageTest() {
        // Given
        when(messageSourceMock.getMessage(validCode, null, Language.getDefaultLocale())).thenReturn(message);

        // When
        String result = translationManager.getMessage(validCode);

        // Then
        assertNotNull(result);
        assertEquals(message, result);
    }

    @Test
    void whenInvalidCodeShouldThrowErrorTest() {
        // Given
        doThrow(new NoSuchMessageException("")).when(messageSourceMock).getMessage(inValidCode, null, Language.getDefaultLocale());

        // Then
        assertThrows(InvalidPropertyException.class, () -> translationManager.getMessage(inValidCode));
    }

}
