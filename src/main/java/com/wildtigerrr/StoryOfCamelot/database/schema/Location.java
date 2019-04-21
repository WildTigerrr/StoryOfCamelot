package com.wildtigerrr.StoryOfCamelot.database.schema;

import javax.persistence.*;

@Entity
@Table(name = "LOCATION")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "FILE_LINK_id")
    private FileLink image_link;
    private Boolean has_stores;

    public Location(FileLink image_link) {
        this.image_link = image_link;
        this.has_stores = false;
    }

    public Location(FileLink image_link, Boolean has_stores) {
        this.image_link = image_link;
        this.has_stores = has_stores;
    }

    public Integer getId() {
        return id;
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
}
