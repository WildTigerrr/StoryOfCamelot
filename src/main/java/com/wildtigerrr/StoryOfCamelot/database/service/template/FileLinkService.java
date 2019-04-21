package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.FileLink;

import java.util.List;

public interface FileLinkService {
    FileLink create(FileLink fileLink);
    void delete(int id);
    FileLink update(FileLink fileLink);
    List<FileLink> getAll();
}
