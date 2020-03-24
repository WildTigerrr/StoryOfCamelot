package com.wildtigerrr.StoryOfCamelot.database.jpa.service.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.BackpackItem;

import java.util.ArrayList;
import java.util.List;

public interface BackpackItemService {
    BackpackItem create(BackpackItem backpackItem);
    void create(ArrayList<BackpackItem> backpackItems);
    void delete(String id);
    BackpackItem update(BackpackItem backpackItem);
    List<BackpackItem> getAll();
}
