package com.wildtigerrr.StoryOfCamelot.database.schema;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "file_link")
@Getter @Setter
public class FileLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Integer id;

    private String fileName;
    private String location;

    protected FileLink() {
    }

    public FileLink(String fileName, String location) {
        this.fileName = fileName;
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
