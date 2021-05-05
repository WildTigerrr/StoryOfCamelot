package com.wildtigerrr.StoryOfCamelot.testutils;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.User;

@Builder
@Data
public class TestUpdateUser {

    @Builder.Default
    private final Integer id = 1;
    @Builder.Default
    private String firstName = "UserFirstName";
    @Builder.Default
    private Boolean isBot = false;
    @Builder.Default
    private String lastName = "UserLastName";
    @Builder.Default
    private String userName = "UserName";
    @Builder.Default
    private String languageCode = Language.getDefaultLocale().getLanguage();

    public User get() {
        return new User(id, firstName, isBot, lastName, userName, languageCode);
    }

}
