package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String systemName;
    @Enumerated(EnumType.STRING)
    private NameTranslation name;
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

    public Location(LocationTemplate locationTemplate) {
        this.systemName = locationTemplate.systemName();
        this.name = locationTemplate.getTranslations();
        this.imageLink = locationTemplate.getFileLink();
        this.hasStores = locationTemplate.hasStores();
    }

    public Integer getId() {
        return id;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getName(Language lang) {
        return name.getName(lang);
    }

    public void setName(NameTranslation name) {
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
                ", nearLocations=" + locationsAsStart.toString() +
                ", imageLink=" + imageLink.toString() +
                ", hasStores=" + hasStores +
                "}\n";
    }
}
