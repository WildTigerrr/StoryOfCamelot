package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;

@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private Boolean hasStores;

    @ManyToOne(optional = true)
    @JoinColumn(name = "filelink_id")
    private FileLink imageLink;

    protected Location() {
    }

    public Location(String locationName, FileLink imageLink) {
        this.name = locationName;
        this.imageLink = imageLink;
        this.hasStores = false;
    }

    public Location(String locationName, FileLink imageLink, Boolean hasStores) {
        this.name = locationName;
        this.imageLink = imageLink;
        this.hasStores = hasStores;
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
        return imageLink;
    }

    public void setImageLink(FileLink imageLink) {
        this.imageLink = imageLink;
    }

    public Boolean getHasStores() {
        return hasStores;
    }

    public void setHasStores(Boolean hasStores) {
        this.hasStores = hasStores;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name=" + name +
                ", imageLink=" + imageLink.toString() +
                ", hasStores=" + hasStores +
                '}';
    }
}
