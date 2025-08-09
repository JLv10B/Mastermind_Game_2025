package com.jl.mastermind.repositories;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.jl.mastermind.entities.Player;


@Repository
public class PlayerRepository {
    private Map<String, Player> playerMap = new ConcurrentHashMap<>();

    public PlayerRepository() {
    }

    public Optional<Player> getPlayerByName(String username) {      
        return Optional.ofNullable(playerMap.get(username.toLowerCase()));
    }

    public Map<String, Player> getPlayerMap() {
        return playerMap;
    }

    public Player createPlayer(Player newPlayer) {
        playerMap.put(newPlayer.getUsername().toLowerCase(), newPlayer);
        return newPlayer;
    }

    public boolean deletePlayer(String targetPlayer) {
        if (playerMap.containsKey(targetPlayer.toLowerCase())) {
            playerMap.remove(targetPlayer.toLowerCase());
            return true;
        } else {
            return false;
        }
    }

    public void deleteAll() {
        playerMap.clear();
    }
}
