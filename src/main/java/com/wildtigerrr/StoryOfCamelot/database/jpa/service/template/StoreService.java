package com.wildtigerrr.StoryOfCamelot.database.jpa.service.template;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Store;

import java.util.HashMap;
import java.util.List;

public interface StoreService {
    Store create(Store store);
    HashMap<String, Store> create(List<Store> stores);
    Store findById(String id);
    void delete(String id);
    Store update(Store store);
    List<Store> getAll();
}
