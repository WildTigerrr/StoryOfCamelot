package com.wildtigerrr.StoryOfCamelot.web.bot.update;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.User;

@Getter
public class Author {

    @Setter
    private String id;
    private String firstName;
    private String lastName;
    private String username;

    public Author(User user) {
        this.id = user.getId().toString();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUserName();
    }

    @Override
    public String toString() {
        return "Author{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

}
