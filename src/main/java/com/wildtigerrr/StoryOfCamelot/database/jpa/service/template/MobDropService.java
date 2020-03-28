package com.wildtigerrr.StoryOfCamelot.database.jpa.service.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.MobDrop;

import java.util.ArrayList;
import java.util.List;

public interface MobDropService {
    MobDrop create(MobDrop mobDrop);
    void create(ArrayList<MobDrop> mobDrops);
    MobDrop findById(String id);
    void delete(String id);
    MobDrop update(MobDrop mobDrop);
    List<MobDrop> getAll();
}