package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.StoreDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Store;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.StoreService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreDao storeDao;

    public StoreServiceImpl(StoreDao storeDao) {
        this.storeDao = storeDao;
    }

    @Override
    public Store create(Store store) {
        Store existingStore = null;
        if (store.getId() != null) {
            Optional<Store> object = storeDao.findById(store.getId());
            if (object.isPresent()) {
                existingStore = object.get();
            }
        } else {
            existingStore = storeDao.save(store);
        }
        return existingStore;
    }

    @Override
    public HashMap<String, Store> create(List<Store> stores) {
        List<Store> newStores = (List<Store>) storeDao.saveAll(stores);
        HashMap<String, Store> storeMap = new HashMap<>();
        for (Store store : newStores) storeMap.put(store.getId(), store);
        return storeMap;
    }

    @Override
    public Store findById(String id) {
        Optional<Store> obj = storeDao.findById(id);
        return obj.orElse(null);
    }

    @Override
    public void delete(String id) {
        storeDao.findById(id).ifPresent(storeDao::delete);
    }

    @Override
    public Store update(Store store) {
        return storeDao.save(store);
    }

    @Override
    public List<Store> getAll() {
        return (List<Store>) storeDao.findAll();
    }

}
