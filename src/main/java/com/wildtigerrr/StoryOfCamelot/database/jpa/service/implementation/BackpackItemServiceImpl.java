package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.BackpackItemDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.BackpackItem;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.BackpackItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BackpackItemServiceImpl implements BackpackItemService {

    private final BackpackItemDao backpackItemDao;

    @Autowired
    public BackpackItemServiceImpl(BackpackItemDao backpackItemDao) {
        this.backpackItemDao = backpackItemDao;
    }

    @Override
    public BackpackItem create(BackpackItem backpackItem) {
        BackpackItem existingItem = null;
        if (backpackItem.getId() != null) {
            Optional<BackpackItem> object = backpackItemDao.findById(backpackItem.getId());
            if (object.isPresent()) {
                existingItem = object.get();
            }
        } else {
            existingItem = backpackItemDao.save(backpackItem);
        }
        return existingItem;
    }

    @Override
    public void create(List<BackpackItem> backpacks) {
        backpackItemDao.saveAll(backpacks);
    }

    @Override
    public void delete(String id) {
        backpackItemDao.findById(id).ifPresent(backpackItemDao::delete);
    }

    @Override
    public BackpackItem update(BackpackItem backpack) {
        return backpackItemDao.save(backpack);
    }

    @Override
    public List<BackpackItem> getAll() {
        return (List<BackpackItem>) backpackItemDao.findAll();
    }
    
}
