package com.wildtigerrr.StoryOfCamelot.database.jpa.service.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Item;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.StoreType;

import java.util.ArrayList;
import java.util.List;

public interface ItemService {
    Item create(Item item);
    void create(ArrayList<Item> items);
    Item findById(String id);
    void delete(String id);
    Item update(Item item);
    List<Item> getAll();
    List<Item> getByStoreTypes(Iterable<StoreType> storeTypes);
}
