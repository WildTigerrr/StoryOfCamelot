package com.wildtigerrr.StoryOfCamelot.database;

import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.web.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseInteraction {

    @Autowired
    private PlayerServiceImpl playerDao;

    @Autowired
    private ResponseHandler responseHandler;

    public void testSavePlayer(String externalId) {
        Player player = new Player(externalId, "WildTigerrr");
        playerDao.create(player);
    }

    public String testGetPlayer(String externalId) {
        try {
            Player player = playerDao.findByExternalId(externalId);
            if (player != null) {
                return player.toString();
            } else {
                return "You don't have a player yet.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
