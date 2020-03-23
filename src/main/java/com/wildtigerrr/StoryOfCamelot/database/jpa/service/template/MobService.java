package com.wildtigerrr.StoryOfCamelot.database.jpa.service.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface MobService {
    Mob create(Mob mob);
    HashMap<String, Mob> create(ArrayList<Mob> mobs);
    Mob findById(String id);
    Mob findBySystemName(String name);
    void delete(String id);
    Mob update(Mob mob);
    List<Mob> getAll();
}
