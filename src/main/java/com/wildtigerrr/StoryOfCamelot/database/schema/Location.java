package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="startLocation")
    private List<LocationNear> locationsAsStart;
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="finishLocation")
    private List<LocationNear> locationsAsFinish;

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

    public Boolean getHasStores() {
        return hasStores;
    }

    public void setHasStores(Boolean hasStores) {
        this.hasStores = hasStores;
    }

    public FileLink getImageLink() {
        return imageLink;
    }

    public void setImageLink(FileLink imageLink) {
        this.imageLink = imageLink;
    }

    public List<LocationNear> getLocationsAsStart() {
        return locationsAsStart;
    }

    public void setLocationsAsStart(List<LocationNear> locationsAsStart) {
        this.locationsAsStart = locationsAsStart;
    }

    public List<LocationNear> getLocationsAsFinish() {
        return locationsAsFinish;
    }

    public void setLocationsAsFinish(List<LocationNear> locationsAsFinish) {
        this.locationsAsFinish = locationsAsFinish;
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
