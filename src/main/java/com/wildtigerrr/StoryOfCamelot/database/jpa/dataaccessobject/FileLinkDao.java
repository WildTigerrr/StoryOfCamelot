package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.FileLink;
import org.springframework.data.repository.CrudRepository;

public interface FileLinkDao  extends CrudRepository<FileLink, String> {

}