package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.database.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.ObjectType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "location")
@Getter @Setter
public class Location extends SimpleObject {

    @Id
    @SequenceGenerator(name = "location_seq", sequenceName = "location_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_seq")
    @GenericGenerator(
            name = "location_seq",
            strategy = "com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = IdGenerator.VALUE_PREFIX_PARAMETER, value = "a0l0")
            })
    @Setter(AccessLevel.NONE)
    private String id;
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

    @Override
    public ObjectType type() {
        return ObjectType.LOCATION;
    }

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
