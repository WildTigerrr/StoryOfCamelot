package com.wildtigerrr.StoryOfCamelot.database.jpa.service.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.MobDrop;

import java.util.List;

public interface MobDropService {
    MobDrop create(MobDrop mobDrop);
    void create(List<MobDrop> mobDrops);
    MobDrop findById(String id);
    List<MobDrop> findByMobId(String mobId);
    void delete(String id);
    MobDrop update(MobDrop mobDrop);
    List<MobDrop> getAll();
}