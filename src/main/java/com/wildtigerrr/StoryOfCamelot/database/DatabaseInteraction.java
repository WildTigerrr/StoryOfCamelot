package com.wildtigerrr.StoryOfCamelot.database;

import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.*;
import com.wildtigerrr.StoryOfCamelot.database.schema.*;
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
//        HashMap<String, FileLink> filesMap = new HashMap<>();
        for (FileLink link : fileLinkService.getAll()) {
            FileLinkTemplate.valueOf(link.getFileName()).setFileLink(link);
//            filesMap.put(link.getFileName(), link);
        }
        HashMap<String, Location> locations = insertLocations();
        insertMobs(locations);
        insertItems();
    }

    private void insertFileLinks() {
        ArrayList<FileLink> initialFileLinks = FileLinkTemplate.getFileLinks();
        fileLinkService.create(initialFileLinks);
    }

    private void insertItems() {
        ArrayList<Item> initialItems = ItemsTemplate.getItems();
        itemService.create(initialItems);
    }

    private void insertMobs(HashMap<String, Location> locations) {
        HashMap<String, Mob> initialMobsMap = MobTemplate.getMobs();
        HashMap<String, ArrayList<String>> locationsMapping = PossibleLocationTemplate.getPossibleLocationsMapping();
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
        mobService.create(new ArrayList<>(initialMobsMap.values()));
    }

    private HashMap<String, Location> insertLocations() {
        HashMap<String, Integer> initialDistances = LocationDistanceTemplate.getLocationDistances();
        HashMap<String, Location> initialLocations = LocationTemplate.getLocations();

        String[] keyParts;
        ArrayList<LocationNear> nearLocations = new ArrayList<>();
        for (String key : initialDistances.keySet()) {
            keyParts = key.split("\\*", 2);
            nearLocations.add(new LocationNear(initialLocations.get(keyParts[0]), initialLocations.get(keyParts[1]), initialDistances.get(key)));
        }
        locationNearService.create(nearLocations);
        return locationService.getAllAsMap();
    }

}
