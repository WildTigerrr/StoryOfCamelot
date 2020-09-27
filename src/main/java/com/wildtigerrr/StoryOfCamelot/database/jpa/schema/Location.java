package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.LocationTemplate;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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
    private Boolean hasEnemies;

    @ManyToOne(optional = true)
    @JoinColumn(name = "filelink_id")
    private FileLink imageLink;

    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL, mappedBy="startLocation")
    private List<LocationNear> locationsAsStart;
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="finishLocation")
    private List<LocationNear> locationsAsFinish;
    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL, mappedBy = "location")
    private Set<Store> stores;

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
        this.hasEnemies = locationTemplate.hasEnemies();
    }

    public String getName(Language lang) {
        return name.getName(lang);
    }
    public String getName(Player player) {
        return getName(player.getLanguage());
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name=" + name +
                (locationsAsStart == null ? "" : ", nearLocations=" + locationsAsStart.toString()) +
                (imageLink == null ? "" : ", imageLink=" + imageLink.toString()) +
                ", hasStores=" + hasStores +
                (hasStores ? ", stores=" + stores : "") +
                "}\n";
    }
}
