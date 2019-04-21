package com.wildtigerrr.StoryOfCamelot.database;

import com.wildtigerrr.StoryOfCamelot.database.schema.FileLink;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.FileLinkServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class DatabaseInteraction {

    @Autowired
    private PlayerServiceImpl playerService;

    @Autowired
    private FileLinkServiceImpl fileLinkService;

    public void insertInitialData() {
        insertFileLinks();
    }

    private void insertFileLinks() {
        ArrayList<FileLink> initialFileLinks = new ArrayList<>(
                Arrays.asList(
                        new FileLink("forest-test", "/locations/forest-test.png")
                        , new FileLink("sword-test", "/items/weapons/swords/sword-test.png")
                )
        );
        fileLinkService.create(initialFileLinks);
    }

}
