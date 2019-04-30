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
    private LocationNearServiceImpl locationNearService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private MobServiceImpl mobService;

    public Player getPlayerById(Integer id) {
        return playerService.findById(id);
    }

    public void updatePlayer(Player player) {
        playerService.update(player);
    }

    public Location getLocationByName(String locationName) {
        return locationService.findByName(locationName);
    }
    public Location getLocationById(int locationId) {
        return locationService.findById(locationId);
    }

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
                        , new FileLink("merchants-square", "images/locations/the-merchants-square.png")
                )
        );
        fileLinkService.create(initialFileLinks);
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

    private HashMap<String, Mob> insertMobs(HashMap<String, FileLink> filesMap, HashMap<String, Location> locations) {
        HashMap<String, Mob> initialMobsMap = getMobs(filesMap);
        HashMap<String, ArrayList<String>> locationsMapping = getPossibleLocationsMapping();
        for (String locationName : locationsMapping.keySet()) {
            for (String mobName : locationsMapping.get(locationName)) {
                initialMobsMap.put(
                        mobName,
                        initialMobsMap.get(mobName).addPossibleLocation(
                                new PossibleLocation(
                                        initialMobsMap.get(mobName),
                                        locations.get(locationName)
                                )
                        )
                );
            }
        }
        return mobService.create(new ArrayList<>(initialMobsMap.values()));
    }

    private HashMap<String, Location> insertLocations(HashMap<String, FileLink> filesMap) {
        HashMap<String, Integer> initialDistances = getLocationDistances();
        HashMap<String, Location> initialLocations = getLocations(filesMap);

        String[] keyParts;
        ArrayList<LocationNear> nearLocations = new ArrayList<>();
        for (String key : initialDistances.keySet()) {
            keyParts = key.split("\\*", 2);
            nearLocations.add(new LocationNear(initialLocations.get(keyParts[0]), initialLocations.get(keyParts[1]), initialDistances.get(key)));
        }
        locationNearService.create(nearLocations);
        return locationService.getAllAsMap();
    }

    private HashMap<String, Location> getLocations(HashMap<String, FileLink> filesMap) {
        return new HashMap<String, Location>() {{
            put("Test Forest", new Location("Test Forest", filesMap.get("forest-test")));
            put("Test Far Forest", new Location("Test Far Forest", filesMap.get("forest-test")));
            put("The Merchants Square", new Location("The Merchants Square", filesMap.get("merchants-square")));
        }};
    }

    private HashMap<String, Integer> getLocationDistances() {
        return new HashMap<String, Integer>() {{
            put("The Merchants Square*Test Forest", 10);
            put("Test Forest*The Merchants Square", 10);
            put("Test Forest*Test Far Forest", 25);
            put("Test Far Forest*Test Forest", 25);
        }};
    }

    private HashMap<String, Mob> getMobs(HashMap<String, FileLink> filesMap) {
        return new HashMap<String, Mob>() {{
            put("Flying Sword", new Mob("Flying Sword", 1, 2, 3, 0, 0, filesMap.get("sword-test")));
            put("Super Flying Sword", new Mob("Super Flying Sword", 2, 2, 7, 2, 2, filesMap.get("sword-test")));
        }};
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
