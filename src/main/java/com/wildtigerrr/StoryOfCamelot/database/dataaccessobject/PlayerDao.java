package com.wildtigerrr.StoryOfCamelot.database.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.dataaccessobject.daointerface.PlayerDaoInterface;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class PlayerDao {/*} implements PlayerDaoInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Player addPlayer(Player player) {
        entityManager.persist(player);
        return player;
    }

    @Override
    public void delete(int id) {
        entityManager.remove(getById(id));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Player getByExternalId(int externalId) {
        String query = "FROM Player AS p WHERE p.external_id = ?";
        List<Player> players = entityManager.createQuery(query).setParameter(1, externalId).getResultList();
        if (!players.isEmpty()) {
            return players.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Player getById(int playerId) {
        return entityManager.find(Player.class, playerId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Player getByNickname(String nickname) {
        String query = "FROM Player AS p WHERE p.nickname = ?";
        List<Player> players = entityManager.createQuery(query).setParameter(1, nickname).getResultList();
        if (!players.isEmpty()) {
            return players.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Player editPlayer(Player playerUpdate) {
        return entityManager.merge(playerUpdate);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Player> getAll() {
        String query = "FROM Player AS p ORDER BY p.Id";
        return (List<Player>) entityManager.createQuery(query).getResultList();
    }*/
}
