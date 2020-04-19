package com.wildtigerrr.StoryOfCamelot.web.bot.update;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.User;

@Getter
public class Author {

    @Setter
    private String id;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String languageCode;

    public Author(User user) {
        this.id = user.getId().toString();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUserName();
        this.languageCode = user.getLanguageCode() == null ? Language.getDefaultLocale().getLanguage() : user.getLanguageCode();
    }

    @Override
    public String toString() {
        return "Author{" +
                "id = " + id + ' ' +
                ", name = " + firstName + " " + lastName +
                ", username = @" + username +
                " }";
    }

}
