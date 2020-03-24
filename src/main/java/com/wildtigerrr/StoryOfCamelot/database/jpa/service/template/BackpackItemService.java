package com.wildtigerrr.StoryOfCamelot.database.jpa.service.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.BackpackItem;

import java.util.List;

public interface BackpackItemService {
    BackpackItem create(BackpackItem backpackItem);
    void create(List<BackpackItem> backpackItems);
    void delete(String id);
    BackpackItem update(BackpackItem backpackItem);
    List<BackpackItem> getAll();
}
