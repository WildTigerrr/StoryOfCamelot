package com.wildtigerrr.StoryOfCamelot.database;

import com.wildtigerrr.StoryOfCamelot.database.schema.FileLink;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.FileLinkServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class DatabaseInteraction {

    @PostConstruct
    public void init() {
        System.out.println("DB fill attempt");
        insertFileLinks();
    }

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
                        new FileLink("forest-test", "images/locations/forest-test.png")
                        , new FileLink("sword-test", "images/items/weapons/swords/sword-test.png")
                )
        );
        fileLinkService.create(initialFileLinks);
    }

}
