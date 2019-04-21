package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.LocationDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.service.template.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationDao locationDao;

    @Override
    public synchronized Location create(Location location) {
        // TODO if Id == null
        Optional object = locationDao.findById(location.getId());
        Location existingLocation;
        if (object.isPresent()) {
            existingLocation = (Location) object.get();
        } else {
            existingLocation = locationDao.save(location);
        }
        return existingLocation;
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
    public Location update(Location location) {
        return locationDao.save(location);
    }

    @Override
    public List<Location> getAll() {
        return (List<Location>) locationDao.findAll();
    }
}
