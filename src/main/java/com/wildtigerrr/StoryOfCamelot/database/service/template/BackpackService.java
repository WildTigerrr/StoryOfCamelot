package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.Backpack;

import java.util.ArrayList;
import java.util.List;

public interface BackpackService {
    Backpack create(Backpack backpack);
    void create(ArrayList<Backpack> backpacks);
    void delete(int id);
    Backpack update(Backpack backpack);
    List<Backpack> getAll();
}
