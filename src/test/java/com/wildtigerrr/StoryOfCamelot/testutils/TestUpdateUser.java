package com.wildtigerrr.StoryOfCamelot.testutils;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.User;

@Builder
@Data
public class TestUpdateUser {

    @Builder.Default
    private final Long id = 1L;
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
    @Builder.Default
    private Boolean canJoinGroups = false;
    @Builder.Default
    private Boolean canReadAllGroupMessages = false;
    @Builder.Default
    private Boolean supportInlineQueries = false;

    public User get() {
        return new User(id, firstName, isBot, lastName, userName, languageCode, canJoinGroups, canReadAllGroupMessages, supportInlineQueries);
    }

}
