package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;

@Entity
@Table(name = "file_link")
public class FileLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String file_name;
    private String location;

    protected FileLink() {
    }

    public FileLink(String fileName, String location) {
        this.file_name = fileName;
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
        return file_name;
    }

    public void setFileName(String fileName) {
        this.file_name = fileName;
    }

    @Override
    public String toString() {
        return "FileLink{" +
                "id=" + id +
                ", location='" + location + '\'' +
                '}';
    }
}
