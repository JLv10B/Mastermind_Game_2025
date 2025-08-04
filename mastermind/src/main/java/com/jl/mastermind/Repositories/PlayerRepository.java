package com.jl.mastermind.Repositories;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.jl.mastermind.Models.Player;

@Repository
public class PlayerRepository {
    private Map<String, Player> playerMap = new ConcurrentHashMap<>();

    public PlayerRepository() {
        playerMap.put("adam", new Player("Adam"));
        playerMap.put("steve", new Player("Steve"));
        playerMap.put("tom", new Player("Tom"));
    }

    public Optional<Player> getPlayerByName(String username) {      
        return Optional.ofNullable(playerMap.get(username.toLowerCase()));
    }

    public Map<String, Player> getPlayerMap() {
        return playerMap;
    }

    public Player createPlayer (Player newPlayer) {
        return playerMap.put(newPlayer.getUsername().toLowerCase(), newPlayer);
    }

    public boolean deletePlayer (String targetPlayer) {
        if (playerMap.containsKey(targetPlayer.toLowerCase())) {
            playerMap.remove(targetPlayer.toLowerCase());
            return true;
        } else {
            return false;
        }
    }
}
