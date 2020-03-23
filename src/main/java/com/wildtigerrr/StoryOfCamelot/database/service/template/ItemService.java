package com.wildtigerrr.StoryOfCamelot.database.service.template;

import com.wildtigerrr.StoryOfCamelot.database.schema.Item;

import java.util.ArrayList;
import java.util.List;

public interface ItemService {
    Item create(Item item);
    void create(ArrayList<Item> items);
    void delete(String id);
    Item update(Item item);
    List<Item> getAll();
}
