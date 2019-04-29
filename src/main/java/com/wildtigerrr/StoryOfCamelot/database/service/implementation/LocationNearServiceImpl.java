package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.LocationNearDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.LocationNear;
import com.wildtigerrr.StoryOfCamelot.database.service.template.LocationNearService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocationNearServiceImpl implements LocationNearService {

    @Autowired
    private LocationNearDao locationNearDao;

    @Override
    public LocationNear create(LocationNear locationNear) {
        LocationNear existingLocationNear = null;
        if (locationNear.getId() != null) {
            Optional object = locationNearDao.findById(locationNear.getId());
            if (object.isPresent()) {
                existingLocationNear = (LocationNear) object.get();
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
    public void delete(int id) {
        locationNearDao.findById(id).ifPresent(locationNear -> locationNearDao.delete(locationNear));
    }

    @Override
    public LocationNear update(LocationNear locationNear) {
        return locationNearDao.save(locationNear);
    }

    @Override
    public List<LocationNear> getAll() {
        return (List<LocationNear>) locationNearDao.findAll();
    }
}
