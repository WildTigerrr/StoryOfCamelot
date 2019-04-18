package com.wildtigerrr.StoryOfCamelot.database;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.PlayerDao;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.web.BotResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseInteraction {

    @Autowired
    private PlayerDao playerDao;

    @Autowired
    private BotResponseHandler responseHandler;

    public void testSavePlayer(String externalId) {
        Player player = new Player(externalId, "WildTigerrr");
        try {
            playerDao.save(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String testGetPlayer(String externalId) {
        try {
            Player player = playerDao.findByExternalId(externalId);
            return player.getNickname();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
