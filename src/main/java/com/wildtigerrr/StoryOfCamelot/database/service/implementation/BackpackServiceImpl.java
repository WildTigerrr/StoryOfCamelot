package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.BackpackDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.service.template.BackpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BackpackServiceImpl implements BackpackService {

    @Autowired
    private BackpackDao backpackDao;

    @Override
    public Backpack create(Backpack backpack) {
        Backpack existingLocation = null;
        if (backpack.getId() != null) {
            Optional object = backpackDao.findById(backpack.getId());
            if (object.isPresent()) {
                existingLocation = (Backpack) object.get();
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
        backpackDao.findById(id).ifPresent(backpack -> backpackDao.delete(backpack));
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
