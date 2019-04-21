package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;

@Entity
@Table(name = "file_link")
public class FileLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String location;

    protected FileLink() {
    }

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

    @Override
    public String toString() {
        return "FileLink{" +
                "id=" + id +
                ", location='" + location + '\'' +
                '}';
    }
}
