package com.wildtigerrr.StoryOfCamelot.database.jpa.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.StoreType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "store")
@Getter @Setter
public class Store extends SimpleObject {

    @Id
    @SequenceGenerator(name = "store_seq", sequenceName = "store_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "store_seq")
    @GenericGenerator(
            name = "store_seq",
            strategy = "com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = IdGenerator.VALUE_PREFIX_PARAMETER, value = "a0s0")
            })
    @Setter(AccessLevel.NONE)
    private String id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id")
    private Location location;
    @ElementCollection(targetClass = StoreType.class, fetch = FetchType.EAGER)
    @JoinTable(name = "STORE_TYPE", joinColumns = @JoinColumn(name = "STORE_ID"))
    @Column(name = "storeType", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<StoreType> storeType;

    protected Store() {
    }

    public Store(Location location, Set<StoreType> storeType) {
        this.location = location;
        this.storeType = storeType;
    }

    @Override
    public ObjectType type() {
        return ObjectType.STORE;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id='" + id + '\'' +
                ", storeType=" + storeType +
                '}';
    }

}
