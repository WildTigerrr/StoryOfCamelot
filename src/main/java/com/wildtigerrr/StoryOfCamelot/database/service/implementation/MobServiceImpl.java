package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.MobDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.Mob;
import com.wildtigerrr.StoryOfCamelot.database.service.template.MobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class MobServiceImpl implements MobService {

    @Autowired
    private MobDao mobDao;

    @Override
    public Mob create(Mob mob) {
        Mob existingLocation = null;
        if (mob.getId() != null) {
            Optional object = mobDao.findById(mob.getId());
            if (object.isPresent()) {
                existingLocation = (Mob) object.get();
            }
        } else {
            existingLocation = mobDao.save(mob);
        }
        return existingLocation;
    }

    @Override
    public HashMap<String, Mob> create(ArrayList<Mob> mobs) {
        List<Mob> newMobs = (List<Mob>) mobDao.saveAll(mobs);
        HashMap<String, Mob> mobsMap = new HashMap<>();
        for (Mob mob : newMobs) mobsMap.put(mob.getName(), mob);
        return mobsMap;
    }

    @Override
    public void delete(int id) {
        mobDao.findById(id).ifPresent(mob -> mobDao.delete(mob));
    }

    @Override
    public Mob update(Mob mob) {
        return mobDao.save(mob);
    }

    @Override
    public List<Mob> getAll() {
        return (List<Mob>) mobDao.findAll();
    }
}
