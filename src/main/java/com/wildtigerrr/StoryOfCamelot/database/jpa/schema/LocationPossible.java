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
@Table(name = "location_possible")
@Getter
public class LocationPossible extends SimpleObject {

    @Id
    @SequenceGenerator(name = "location_possible_seq", sequenceName = "location_possible_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_possible_seq")
    @GenericGenerator(
            name = "location_possible_seq",
            strategy = "com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = IdGenerator.VALUE_PREFIX_PARAMETER, value = "a0pl")
            })
    @Setter(AccessLevel.NONE)
    private String id;
    @ManyToOne
    @JoinColumn(name = "mob_id")
    private Mob mob;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Override
    public ObjectType type() {
        return ObjectType.LOCATION_POSSIBLE;
    }

    protected LocationPossible() {
    }

    public LocationPossible(Mob mob, Location location) {
        this.mob = mob;
        this.location = location;
    }

}
