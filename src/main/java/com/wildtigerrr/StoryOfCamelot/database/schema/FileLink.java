package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;

@Entity
@Table(name = "FILE_LINK")
public class FileLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String location;

    public FileLink(String location) {
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
