package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.BackpackDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.BackpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BackpackServiceImpl implements BackpackService {

    private final BackpackDao backpackDao;

    @Autowired
    public BackpackServiceImpl(BackpackDao backpackDao) {
        this.backpackDao = backpackDao;
    }

    @Override
    public Backpack create(Backpack backpack) {
        Backpack existingLocation = null;
        if (backpack.getId() != null) {
            Optional<Backpack> object = backpackDao.findById(backpack.getId());
            if (object.isPresent()) {
                existingLocation = object.get();
            }
        } else {
            existingLocation = backpackDao.save(backpack);
        }
        return existingLocation;
    }

    @Override
    public void create(ArrayList<Backpack> backpacks) {
        backpackDao.saveAll(backpacks);
    }

    @Override
    public void delete(String id) {
        backpackDao.findById(id).ifPresent(backpackDao::delete);
    }

    @Override
    public Backpack update(Backpack backpack) {
        return backpackDao.save(backpack);
    }

    @Override
    public List<Backpack> getAll() {
        return (List<Backpack>) backpackDao.findAll();
    }
}
