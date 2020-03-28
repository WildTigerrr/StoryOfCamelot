package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.MobDropDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.MobDrop;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.MobDropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MobDropServiceImpl implements MobDropService {

    private final MobDropDao mobDropDao;

    @Autowired
    public MobDropServiceImpl(MobDropDao mobDropDao) {
        this.mobDropDao = mobDropDao;
    }

    @Override
    public MobDrop create(MobDrop mobDrop) {
        MobDrop existingMobDrop = null;
        if (mobDrop.getId() != null) {
            Optional<MobDrop> object = mobDropDao.findById(mobDrop.getId());
            if (object.isPresent()) {
                existingMobDrop = object.get();
            }
        } else {
            existingMobDrop = mobDropDao.save(mobDrop);
        }
        return existingMobDrop;
    }

    @Override
    public void create(ArrayList<MobDrop> mobDrops) {
        mobDropDao.saveAll(mobDrops);
    }

    @Override
    public MobDrop findById(String id) {
        Optional<MobDrop> obj = mobDropDao.findById(id);
        return obj.orElse(null);
    }

    @Override
    public void delete(String id) {
        mobDropDao.findById(id).ifPresent(mobDropDao::delete);
    }

    @Override
    public MobDrop update(MobDrop mobDrop) {
        return mobDropDao.save(mobDrop);
    }

    @Override
    public List<MobDrop> getAll() {
        return (List<MobDrop>) mobDropDao.findAll();
    }
}
