package com.wildtigerrr.StoryOfCamelot.database.jpa;

import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.*;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.*;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.StoreType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.*;
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

    private final FileLinkService fileLinkService;
    private final LocationService locationService;
    private final LocationNearService locationNearService;
    private final ItemService itemService;
    private final MobService mobService;
    private final MobDropService mobDropService;
    private final StoreService storeService;

    @Autowired
    public DatabaseInteraction(FileLinkService fileLinkService, LocationService locationService, LocationNearService locationNearService, ItemService itemService, MobService mobService, MobDropService mobDropService, StoreService storeService) {
        this.fileLinkService = fileLinkService;
        this.locationService = locationService;
        this.locationNearService = locationNearService;
        this.itemService = itemService;
        this.mobService = mobService;
        this.mobDropService = mobDropService;
        this.storeService = storeService;
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
        HashMap<String, Mob> mobs = insertMobs(locations);
        HashMap<String, Item> items = insertItems();
        insertDropMap(mobs, items);
        insertStores(locations);
        log.debug("Database Initialized");
    }

    private void insertFileLinks() {
        log.debug("Inserting File links");
        ArrayList<FileLink> initialFileLinks = FileLinkTemplate.getFileLinks();
        fileLinkService.create(initialFileLinks);
    }

    private void insertDropMap(HashMap<String, Mob> mobs, HashMap<String, Item> items) {
        log.debug("Inserting Drop map");
        List<MobDrop> drops = new ArrayList<>();
        for (DropTemplate template : DropTemplate.values()) {
            drops.add(new MobDrop(mobs.get(template.getMobName()), items.get(template.getItemName()))
                    .setQuantityRandom(template.getQuantityRandom(), template.getQuantityMin(), template.getQuantityLimit())
                    .setDurabilityRandom(template.getDurabilityRandom(), template.getDurabilityMin(), template.getDurabilityMax()));
        }
        mobDropService.create(drops);
    }

    private HashMap<String, Item> insertItems() {
        log.debug("Inserting Items");
        HashMap<String, Item> initialItemsMap = ItemTemplate.getItems();
        itemService.create(new ArrayList<>(initialItemsMap.values()));
        return initialItemsMap;
    }

    private HashMap<String, Mob> insertMobs(HashMap<String, Location> locations) {
        log.debug("Inserting Mobs");
        HashMap<String, Mob> initialMobsMap = MobTemplate.getMobs();
        HashMap<String, HashMap<String, Integer>> locationsMapping = PossibleLocationTemplate.getPossibleLocationsMapping();
        for (String locationName : locationsMapping.keySet()) {
            for (String mobName : locationsMapping.get(locationName).keySet()) {
                initialMobsMap.put(
                        mobName,
                        initialMobsMap.get(mobName).addPossibleLocation(
                                new LocationPossible(
                                        initialMobsMap.get(mobName),
                                        locations.get(locationName),
                                        locationsMapping.get(locationName).get(mobName)
                                )
                        )
                );
            }
        }
        mobService.create(new ArrayList<>(initialMobsMap.values()));
        return initialMobsMap;
    }

    private void insertStores(HashMap<String, Location> locations) {
        log.debug("Inserting Stores");
        List<Store> stores = new ArrayList<>();
        Map<String, List<Set<StoreType>>> storesMapping = StoreTemplate.getStoreMapping();
        for (String locationName : locations.keySet()) {
            if (storesMapping.containsKey(locationName)) {
                for (Set<StoreType> storeTypes : storesMapping.get(locationName)) {
                    stores.add(new Store(locations.get(locationName), storeTypes));
                }
            }
        }
        storeService.create(stores);
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
