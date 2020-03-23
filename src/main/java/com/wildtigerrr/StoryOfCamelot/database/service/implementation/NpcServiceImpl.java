package com.wildtigerrr.StoryOfCamelot.database.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.NpcDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.Npc;
import com.wildtigerrr.StoryOfCamelot.database.service.template.NpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NpcServiceImpl implements NpcService {

    @Autowired
    private NpcDao npcDao;

    @Override
    public Npc create(Npc npc) {
        Npc existingNpc = null;
        if (npc.getId() != null) {
            Optional object = npcDao.findById(npc.getId());
            if (object.isPresent()) {
                existingNpc = (Npc) object.get();
            }
        } else {
            existingNpc = npcDao.save(npc);
        }
        return existingNpc;
    }

    @Override
    public void create(ArrayList<Npc> npcs) {
        npcDao.saveAll(npcs);
    }

    @Override
    public void delete(String id) {
        npcDao.findById(id).ifPresent(npc -> npcDao.delete(npc));
    }

    @Override
    public Npc update(Npc npc) {
        return npcDao.save(npc);
    }

    @Override
    public List<Npc> getAll() {
        return (List<Npc>) npcDao.findAll();
    }
}
