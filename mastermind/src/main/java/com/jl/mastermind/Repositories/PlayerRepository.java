package com.jl.mastermind.Repositories;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.jl.mastermind.Exceptions.UsernameAlreadyExistsException;
import com.jl.mastermind.Models.Player;

@Repository
public class PlayerRepository {
    private Map<String, Player> playerMap = new ConcurrentHashMap<>();

    public PlayerRepository() {
        playerMap.put("Adam", new Player("Adam"));
        playerMap.put("Steve", new Player("Steve"));
        playerMap.put("Tom", new Player("Tom"));
    }

    public Optional<Player> getPlayerByName(String username) {      
        return Optional.ofNullable(playerMap.get(username.toLowerCase()));
    }

    public Map<String, Player> getPlayerMap() {
        return playerMap;
    }

    public Player createPlayer (Player newPlayer) {
        if (playerMap.containsKey(newPlayer.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists, please enter a different username");
        }
        else {
            playerMap.put(newPlayer.getUsername().toLowerCase(), newPlayer);
        }
        return newPlayer;
    }
}
