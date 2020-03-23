package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.NpcDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Npc;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.NpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NpcServiceImpl implements NpcService {

    private final NpcDao npcDao;

    @Autowired
    public NpcServiceImpl(NpcDao npcDao) {
        this.npcDao = npcDao;
    }

    @Override
    public Npc create(Npc npc) {
        Npc existingNpc = null;
        if (npc.getId() != null) {
            Optional<Npc> object = npcDao.findById(npc.getId());
            if (object.isPresent()) {
                existingNpc = object.get();
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
        npcDao.findById(id).ifPresent(npcDao::delete);
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
