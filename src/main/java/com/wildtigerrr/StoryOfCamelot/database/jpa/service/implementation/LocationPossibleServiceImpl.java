package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.LocationPossibleDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationNear;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationPossible;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.LocationPossibleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationPossibleServiceImpl implements LocationPossibleService {

    private final LocationPossibleDao possibleLocationDao;

    @Autowired
    public LocationPossibleServiceImpl(LocationPossibleDao possibleLocationDao) {
        this.possibleLocationDao = possibleLocationDao;
    }

    @Override
    public LocationPossible create(LocationPossible possibleLocation) {
        LocationPossible existingPossibleLocation = null;
        if (possibleLocation.getId() != null) {
            Optional<LocationPossible> object = possibleLocationDao.findById(possibleLocation.getId());
            if (object.isPresent()) {
                existingPossibleLocation = object.get();
            }
        } else {
            existingPossibleLocation = possibleLocationDao.save(possibleLocation);
        }
        return existingPossibleLocation;
    }

    @Override
    public void create(ArrayList<LocationPossible> possibleLocations) {
        possibleLocationDao.saveAll(possibleLocations);
    }

    @Override
    public void delete(String id) {
        possibleLocationDao.findById(id).ifPresent(possibleLocationDao::delete);
    }

    @Override
    public LocationPossible update(LocationPossible possibleLocation) {
        return possibleLocationDao.save(possibleLocation);
    }

    @Override
    public List<LocationPossible> getAll() {
        return (List<LocationPossible>) possibleLocationDao.findAll();
    }

    @Override
    public List<LocationPossible> getPossibleMobs(Location location) {
        return possibleLocationDao.findAllByLocation(location);
    }
}
