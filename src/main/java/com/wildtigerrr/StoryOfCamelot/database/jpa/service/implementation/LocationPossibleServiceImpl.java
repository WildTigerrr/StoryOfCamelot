package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.LocationPossibleDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationPossible;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.LocationPossibleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationPossibleServiceImpl implements LocationPossibleService {

    @Autowired
    private LocationPossibleDao possibleLocationDao;

    @Override
    public LocationPossible create(LocationPossible possibleLocation) {
        LocationPossible existingPossibleLocation = null;
        if (possibleLocation.getId() != null) {
            Optional object = possibleLocationDao.findById(possibleLocation.getId());
            if (object.isPresent()) {
                existingPossibleLocation = (LocationPossible) object.get();
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
        possibleLocationDao.findById(id).ifPresent(possibleLocation -> possibleLocationDao.delete(possibleLocation));
    }

    @Override
    public LocationPossible update(LocationPossible possibleLocation) {
        return possibleLocationDao.save(possibleLocation);
    }

    @Override
    public List<LocationPossible> getAll() {
        return (List<LocationPossible>) possibleLocationDao.findAll();
    }
}
