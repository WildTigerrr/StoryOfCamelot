package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "location")
@Getter @Setter
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
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
        this.systemName = locationTemplate.name();
        this.name = locationTemplate.getTranslations();
        this.imageLink = locationTemplate.getFileLink();
        this.hasStores = locationTemplate.hasStores();
    }

    public String getName(Language lang) {
        return name.getName(lang);
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name=" + name +
                (locationsAsStart == null ? "" : ", nearLocations=" + locationsAsStart.toString()) +
                ", imageLink=" + imageLink.toString() +
                ", hasStores=" + hasStores +
                "}\n";
    }
}
