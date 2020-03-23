package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.schema.FileLink;
import org.springframework.data.repository.CrudRepository;

public interface FileLinkDao  extends CrudRepository<FileLink, String> {

}