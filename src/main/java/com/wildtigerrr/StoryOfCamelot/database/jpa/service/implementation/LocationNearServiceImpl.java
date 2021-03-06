package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.LocationNearDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationNear;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.LocationNearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class LocationNearServiceImpl implements LocationNearService {

    private final LocationNearDao locationNearDao;

    @Autowired
    public LocationNearServiceImpl(LocationNearDao locationNearDao) {
        this.locationNearDao = locationNearDao;
    }

    @Override
    public LocationNear create(LocationNear locationNear) {
        LocationNear existingLocationNear = null;
        if (locationNear.getId() != null) {
            Optional<LocationNear> object = locationNearDao.findById(locationNear.getId());
            if (object.isPresent()) {
                existingLocationNear = object.get();
            }
        } else {
            existingLocationNear = locationNearDao.save(locationNear);
        }
        return existingLocationNear;
    }

    @Override
    public void create(ArrayList<LocationNear> locationNear) {
        locationNearDao.saveAll(locationNear);
    }

    @Override
    public void delete(String id) {
        locationNearDao.findById(id).ifPresent(locationNearDao::delete);
    }

    @Override
    public LocationNear update(LocationNear locationNear) {
        return locationNearDao.save(locationNear);
    }

    @Override
    public ArrayList<Location> getNearLocations(Location location) {
        ArrayList<LocationNear> nearLocations = (ArrayList<LocationNear>) locationNearDao.findByStartLocation(location);
        ArrayList<Location> locations = new ArrayList<>();
        for (LocationNear nearLocation : nearLocations) {
            locations.add(nearLocation.getFinishLocation());
        }
        return locations;
    }

    @Override
    public int getDistance(Location start, Location finish) {
        LocationNear locationNear = locationNearDao.findByStartLocationAndFinishLocation(start, finish);
        if (locationNear != null) {
            return locationNear.getDistance();
        } else {
            return -1;
        }
    }

    @Override
    public ArrayList<LocationNear> getAll() {
        return (ArrayList<LocationNear>) locationNearDao.findAll();
    }
}
