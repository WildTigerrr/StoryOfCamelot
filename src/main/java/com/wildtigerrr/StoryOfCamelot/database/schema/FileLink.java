package com.wildtigerrr.StoryOfCamelot.database.schema;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator;
import com.wildtigerrr.StoryOfCamelot.database.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.ObjectType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "file_link")
@Getter @Setter
public class FileLink extends SimpleObject {

    @Id
    @SequenceGenerator(name = "file_link_seq", sequenceName = "file_link_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_link_seq")
    @GenericGenerator(
            name = "file_link_seq",
            strategy = "com.wildtigerrr.StoryOfCamelot.bin.base.service.IdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = IdGenerator.VALUE_PREFIX_PARAMETER, value = "a0f0")
            })
    @Setter(AccessLevel.NONE)
    private String id;

    private String fileName;
    private String location;

    @Override
    public ObjectType type() {
        return ObjectType.FILE_LINK;
    }

    protected FileLink() {
    }

    public FileLink(String fileName, String location) {
        this.fileName = fileName;
        this.location = location;
    }

    @Override
    public String toString() {
        return "FileLink{" +
                "id=" + id +
                ", location='" + location + '\'' +
                '}';
    }
}
