package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;

@Entity
@Table(name = "file_link")
public class FileLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String fileName;
    private String location;

    protected FileLink() {
    }

    public FileLink(String fileName, String location) {
        this.fileName = fileName;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "FileLink{" +
                "id=" + id +
                ", location='" + location + '\'' +
                '}';
    }
}
