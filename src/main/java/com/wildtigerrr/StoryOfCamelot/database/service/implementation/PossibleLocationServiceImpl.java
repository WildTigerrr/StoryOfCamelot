package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.PossibleLocationDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.PossibleLocation;
import com.wildtigerrr.StoryOfCamelot.database.service.template.PossibleLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PossibleLocationServiceImpl implements PossibleLocationService {

    @Autowired
    private PossibleLocationDao possibleLocationDao;

    @Override
    public PossibleLocation create(PossibleLocation possibleLocation) {
        PossibleLocation existingPossibleLocation = null;
        if (possibleLocation.getId() != null) {
            Optional object = possibleLocationDao.findById(possibleLocation.getId());
            if (object.isPresent()) {
                existingPossibleLocation = (PossibleLocation) object.get();
            }
        } else {
            existingPossibleLocation = possibleLocationDao.save(possibleLocation);
        }
        return existingPossibleLocation;
    }

    @Override
    public void create(ArrayList<PossibleLocation> possibleLocations) {
        possibleLocationDao.saveAll(possibleLocations);
    }

    @Override
    public void delete(int id) {
        possibleLocationDao.findById(id).ifPresent(possibleLocation -> possibleLocationDao.delete(possibleLocation));
    }

    @Override
    public PossibleLocation update(PossibleLocation possibleLocation) {
        return possibleLocationDao.save(possibleLocation);
    }

    @Override
    public List<PossibleLocation> getAll() {
        return (List<PossibleLocation>) possibleLocationDao.findAll();
    }
}
