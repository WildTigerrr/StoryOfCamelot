package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.Npc;

import java.util.ArrayList;
import java.util.List;

public interface NpcService {
    Npc create(Npc npc);
    void create(ArrayList<Npc> npcs);
    void delete(String id);
    Npc update(Npc npc);
    List<Npc> getAll();
}
