package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.FileLink;

import java.util.ArrayList;
import java.util.List;

public interface FileLinkService {
    FileLink create(FileLink fileLink);
    void create(ArrayList<FileLink> fileLinks);
    void delete(String id);
    FileLink update(FileLink fileLink);
    List<FileLink> getAll();
}
