package com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation;

import com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject.BackpackDao;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.BackpackType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.BackpackService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BackpackServiceImpl implements BackpackService {

    private final BackpackDao backpackDao;
    private final PlayerService playerService;

    @Autowired
    public BackpackServiceImpl(BackpackDao backpackDao, PlayerService playerService) {
        this.backpackDao = backpackDao;
        this.playerService = playerService;
    }

    @Override
    public Backpack create(Backpack backpack) {
        if (backpack.getId() != null) {
            Optional<Backpack> object = backpackDao.findById(backpack.getId());
            return object.orElseThrow(() -> new InvalidInputException("Attempt to create Backpack with Id: " + backpack.getId()));
        } else {
            validateSingleMainBackpack(backpack);
            return backpackDao.save(backpack);
        }
    }

    @Override
    public void create(List<Backpack> backpacks) {
        backpackDao.saveAll(backpacks);
    }

    @Override
    public Backpack findByPlayerId(String playerId) {
        Optional<Backpack> obj = backpackDao.findByPlayerId(playerId);
        return obj.orElse(null);
    }

    @Override
    public Backpack findMainByPlayerId(String playerId) {
        Optional<Backpack> obj = backpackDao.findByPlayerIdAndType(playerId, BackpackType.MAIN);
        if (obj.isPresent()) {
            return obj.get();
        } else {
            Player player = playerService.findById(playerId);
            return create(new Backpack(player));
        }
    }

    @Override
    public void delete(String id) {
        backpackDao.findById(id).ifPresent(backpackDao::delete);
    }

    @Override
    public Backpack update(Backpack backpack) {
        return backpackDao.save(backpack);
    }

    @Override
    public List<Backpack> getAll() {
        return (List<Backpack>) backpackDao.findAll();
    }

    private void validateSingleMainBackpack(Backpack backpack) {
        if (backpack.getType() == BackpackType.MAIN) {
            Optional<Backpack> obj = backpackDao.findByPlayerIdAndType(backpack.getPlayer().getId(), BackpackType.MAIN);
            if (obj.isPresent()) {
                throw new InvalidInputException("Only one Main Backpack can exist for Player " + backpack.getPlayer().getId());
            }
        }
    }

}
