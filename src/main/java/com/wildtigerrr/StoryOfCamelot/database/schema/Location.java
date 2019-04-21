package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;

@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private Boolean has_stores;

    @ManyToOne(optional = true)
    @JoinColumn(name = "filelink_id")
    private FileLink image_link;

    protected Location() {
    }

    public Location(String locationName, FileLink image_link) {
        this.name = locationName;
        this.image_link = image_link;
        this.has_stores = false;
    }

    public Location(String locationName, FileLink image_link, Boolean has_stores) {
        this.name = locationName;
        this.image_link = image_link;
        this.has_stores = has_stores;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileLink getImageLink() {
        return image_link;
    }

    public void setImageLink(FileLink image_link) {
        this.image_link = image_link;
    }

    public Boolean getHasStores() {
        return has_stores;
    }

    public void setHasStores(Boolean has_stores) {
        this.has_stores = has_stores;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name=" + name +
                ", image_link=" + image_link.toString() +
                ", has_stores=" + has_stores +
                '}';
    }
}
