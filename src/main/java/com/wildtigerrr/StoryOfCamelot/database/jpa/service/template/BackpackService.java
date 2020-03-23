package com.wildtigerrr.StoryOfCamelot.database.jpa.service.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;

import java.util.ArrayList;
import java.util.List;

public interface BackpackService {
    Backpack create(Backpack backpack);
    void create(ArrayList<Backpack> backpacks);
    void delete(String id);
    Backpack update(Backpack backpack);
    List<Backpack> getAll();
}
