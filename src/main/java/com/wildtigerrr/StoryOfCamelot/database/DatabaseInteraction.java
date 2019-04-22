package com.wildtigerrr.StoryOfCamelot.database;

import com.wildtigerrr.StoryOfCamelot.database.schema.*;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.ItemQuality;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.ItemSubType;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class DatabaseInteraction {

    @PostConstruct
    public void init() {
        System.out.println("DB fill");
        insertInitialData();
    }

    @Autowired
    private PlayerServiceImpl playerService;

    @Autowired
    private FileLinkServiceImpl fileLinkService;

    @Autowired
    private LocationServiceImpl locationService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private MobServiceImpl mobService;

    private void insertInitialData() {
        insertFileLinks();
        List<FileLink> fileLinks = fileLinkService.getAll();
        HashMap<String, FileLink> filesMap = new HashMap<>();
        for (FileLink link : fileLinks) filesMap.put(link.getFileName(), link);
        HashMap<String, Location> locations = insertLocations(filesMap);
        insertMobs(filesMap, locations);
        insertItems(filesMap);

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

    private HashMap<String, Location> insertLocations(HashMap<String, FileLink> filesMap) {
        ArrayList<Location> initialLocations = new ArrayList<>(
                Arrays.asList(
                        new Location("Test Forest", filesMap.get("forest-test"))
                        , new Location("Test Far Forest", filesMap.get("forest-test"))
                )
        );
        return locationService.create(initialLocations);
    }

    private void insertItems(HashMap<String, FileLink> filesMap) {
        ArrayList<Item> initialItems = new ArrayList<>(
                Arrays.asList(
                        new Item(10.0, 100, 10.0, ItemSubType.SWORD, ItemQuality.COMMON, filesMap.get("sword-test"))
                        , new Item(15.0, 150, 25.0, ItemSubType.SWORD, ItemQuality.UNCOMMON, filesMap.get("sword-test"))
                )
        );
        itemService.create(initialItems);
    }

    private HashMap<String, Mob> insertMobs(HashMap<String, FileLink> filesMap, HashMap<String, Location> loctions) {
        /*ArrayList<Mob> initialMobs = new ArrayList<>(
                Arrays.asList(
                        new Mob("Flying Sword", 1, 2, 3, 0, 0, filesMap.get("sword-test"))
                        , new Mob("Super Flying Sword", 2, 2, 7, 2, 2, filesMap.get("sword-test"))
                )
        );*/
        HashMap<String, Mob> initialMobsMap = new HashMap<String, Mob>() {{
            put("Flying Sword", new Mob("Flying Sword", 1, 2, 3, 0, 0, filesMap.get("sword-test")));
            put("Super Flying Sword", new Mob("Super Flying Sword", 2, 2, 7, 2, 2, filesMap.get("sword-test")));
        }};
        HashMap<String, ArrayList<String>> locationsMapping = getPossibleLocationsMapping();
        for (String locationName : locationsMapping.keySet()) {
            for (String mobName : locationsMapping.get(locationName)) {
                initialMobsMap.put(
                        mobName,
                        initialMobsMap.get(mobName).addPossibleLocation(
                                new PossibleLocation(
                                        initialMobsMap.get(mobName),
                                        loctions.get(locationName)
                                )
                        )
                );
            }
        }
        return mobService.create(new ArrayList<Mob>(initialMobsMap.values()));
    }

    private HashMap<String, ArrayList<String>> getPossibleLocationsMapping() {
        return new HashMap<String, ArrayList<String>>() {{
            put("Test Forest", new ArrayList<String>() {{
                add("Flying Sword");
            }});
            put("Test Far Forest", new ArrayList<String>() {{
                add("Flying Sword");
                add("Super Flying Sword");
            }});
        }};
    }

}
