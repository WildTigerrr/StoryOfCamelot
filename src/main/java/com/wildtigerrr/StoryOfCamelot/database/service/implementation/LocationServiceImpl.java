package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.LocationDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.service.template.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationDao locationDao;

    @Override
    public synchronized Location create(Location location) {
        Location existingLocation = null;
        if (location.getId() != null) {
            Optional object = locationDao.findById(location.getId());
            if (object.isPresent()) {
                existingLocation = (Location) object.get();
            }
        } else {
            existingLocation = locationDao.save(location);
        }
        return existingLocation;
    }

    @Override
    public HashMap<String, Location> create(ArrayList<Location> locations) {
        List<Location> newLocations = (List<Location>) locationDao.saveAll(locations);
        HashMap<String, Location> locationsMap = new HashMap<>();
        for (Location loc : newLocations) locationsMap.put(loc.getSystemName(), loc);
        return locationsMap;
    }

    @Override
    public void delete(int id) {
        locationDao.findById(id).ifPresent(location -> locationDao.delete(location));
    }

    @Override
    public Location findById(int id) {
        Optional object = locationDao.findById(id);
        if (object.isPresent()) {
            return  (Location) object.get();
        }
        return null;
    }

    @Override
    public Location findByName(String locationName) {
        return locationDao.findBySystemName(locationName);
    }

    @Override
    public Location update(Location location) {
        return locationDao.save(location);
    }

    @Override
    public List<Location> getAll() {
        return (List<Location>) locationDao.findAll();
    }

    @Override
    public HashMap<String, Location> getAllAsMap() {
        HashMap<String, Location> locationsMap = new HashMap<>();
        for (Location location : getAll()) locationsMap.put(location.getSystemName(), location);
        return locationsMap;
    }
}
