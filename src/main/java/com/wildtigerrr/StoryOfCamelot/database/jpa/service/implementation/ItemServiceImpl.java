package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.ItemDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Item;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.StoreType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Override
    public Item create(Item item) {
        Item existingItem = null;
        if (item.getId() != null) {
            Optional<Item> object = itemDao.findById(item.getId());
            if (object.isPresent()) {
                existingItem = object.get();
            }
        } else {
            existingItem = itemDao.save(item);
        }
        return existingItem;
    }

    @Override
    public void create(ArrayList<Item> items) {
        itemDao.saveAll(items);
    }

    @Override
    public Item findById(String id) {
        return itemDao.findById(id).orElse(null);
    }

    @Override
    public void delete(String id) {
        itemDao.findById(id).ifPresent(itemDao::delete);
    }

    @Override
    public Item update(Item item) {
        return itemDao.save(item);
    }

    @Override
    public List<Item> getAll() {
        return (List<Item>) itemDao.findAll();
    }

    @Override
    public List<Item> getByStoreTypes(Iterable<StoreType> storeTypes) {
        return itemDao.findAllByStoreTypeIn(storeTypes);
    }
}
