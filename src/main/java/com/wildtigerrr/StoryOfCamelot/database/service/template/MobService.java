package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.Mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface MobService {
    Mob create(Mob mob);
    HashMap<String, Mob> create(ArrayList<Mob> mobs);
    Mob findById(int id);
    void delete(int id);
    Mob update(Mob mob);
    List<Mob> getAll();
}
