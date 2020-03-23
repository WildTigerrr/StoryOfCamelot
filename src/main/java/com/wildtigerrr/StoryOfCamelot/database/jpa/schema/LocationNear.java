package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "location_near")
@Getter @Setter
public class LocationNear extends SimpleObject {

    @Id
    @SequenceGenerator(name = "location_near_seq", sequenceName = "location_near_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_near_seq")
    @GenericGenerator(
            name = "location_near_seq",
            strategy = "com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = IdGenerator.VALUE_PREFIX_PARAMETER, value = "a0ln")
            })
    @Setter(AccessLevel.NONE)
    private String id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "startLocation_id", nullable = false)
    private Location startLocation;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "finishLocation_id", nullable = false)
    private Location finishLocation;

    private int distance;

    @Override
    public ObjectType type() {
        return ObjectType.LOCATION_NEAR;
    }

    protected LocationNear() {
    }

    public LocationNear(Location start, Location finish, int distance) {
        this.startLocation = start;
        this.finishLocation = finish;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "LocationNear{" +
                "id=" + id +
                ", finishLocation=" + finishLocation.getSystemName() +
                ", distance=" + distance +
                '}';
    }
}