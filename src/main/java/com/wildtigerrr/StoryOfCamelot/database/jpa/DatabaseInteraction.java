package com.wildtigerrr.StoryOfCamelot.database.jpa;

import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.*;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.*;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Log4j2
@Service
@DependsOn({"applicationContextProvider"})
public class DatabaseInteraction {

    private final FileLinkServiceImpl fileLinkService;

    private final LocationServiceImpl locationService;

    private final LocationNearServiceImpl locationNearService;

    private final ItemServiceImpl itemService;

    private final MobServiceImpl mobService;

    @Autowired
    public DatabaseInteraction(FileLinkServiceImpl fileLinkService, LocationServiceImpl locationService, LocationNearServiceImpl locationNearService, ItemServiceImpl itemService, MobServiceImpl mobService) {
        this.fileLinkService = fileLinkService;
        this.locationService = locationService;
        this.locationNearService = locationNearService;
        this.itemService = itemService;
        this.mobService = mobService;
    }

    @PostConstruct
    public void init() {
        insertInitialData();
    }

    private void insertInitialData() {
        log.debug("Inserting initial DB data");
        insertFileLinks();
        for (FileLink link : fileLinkService.getAll()) {
            FileLinkTemplate.valueOf(link.getFileName()).setFileLink(link);
        }
        HashMap<String, Location> locations = insertLocations();
        insertMobs(locations);
        insertItems();
        log.debug("Database Initialized");
    }

    private void insertFileLinks() {
        log.debug("Inserting File links");
        ArrayList<FileLink> initialFileLinks = FileLinkTemplate.getFileLinks();
        fileLinkService.create(initialFileLinks);
    }

    private void insertItems() {
        log.debug("Inserting Items");
        ArrayList<Item> initialItems = ItemsTemplate.getItems();
        itemService.create(initialItems);
    }

    private void insertMobs(HashMap<String, Location> locations) {
        log.debug("Inserting Mobs");
        HashMap<String, Mob> initialMobsMap = MobTemplate.getMobs();
        HashMap<String, ArrayList<String>> locationsMapping = PossibleLocationTemplate.getPossibleLocationsMapping();
        for (String locationName : locationsMapping.keySet()) {
            for (String mobName : locationsMapping.get(locationName)) {
                initialMobsMap.put(
                        mobName,
                        initialMobsMap.get(mobName).addPossibleLocation(
                                new LocationPossible(
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
        log.debug("Inserting Locations");
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