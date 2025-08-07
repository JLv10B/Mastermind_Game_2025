package com.jl.mastermind.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jl.mastermind.exceptions.InsufficientPermissionsException;
import com.jl.mastermind.exceptions.NameAlreadyExistsException;
import com.jl.mastermind.exceptions.ResourceNotFoundException;
import com.jl.mastermind.repositories.PlayerRepository;
import com.jl.mastermind.entities.Player;

import jakarta.servlet.http.HttpSession;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Map<String, Player> getAllPlayers() {
        return playerRepository.getPlayerMap();
    }
    
    
    public Player getPlayerByName(String username) {
        Optional<Player> playerOptional = playerRepository.getPlayerByName(username.toLowerCase());
        if (playerOptional.isPresent()) {
            return playerOptional.get();
       } else {
            throw new ResourceNotFoundException(username + " not found");
       }
    }


    public boolean deletePlayer(String username) {
        Optional<Player> playerOptional = playerRepository.getPlayerByName(username.toLowerCase());
        if (!playerOptional.isPresent()) {
            throw new ResourceNotFoundException(username + " not found");
        } else {
            return playerRepository.deletePlayer(username.toLowerCase());
        }
    }


    // public Player createPlayer(Player newPlayer, HttpSession session) {
    //     if (session.getAttribute("username") != null) {
    //         throw new InsufficientPermissionsException("You are alreayd logged in.");
    //     }
    //     Optional<Player> playerOptional = playerRepository.getPlayerByName(newPlayer.getUsername().toLowerCase());
    //     if (playerOptional.isPresent()){
    //         throw new NameAlreadyExistsException(newPlayer.getUsername() + " already exists");
    //     } else {
    //         Player createdPlayer = playerRepository.createPlayer(newPlayer);
    //         session.setAttribute("username", createdPlayer.getUsername());
    //         return createdPlayer;
    //     }
    // }

    public Player getOrCreatePlayer(Player newPlayer, HttpSession session) {
        Optional<Player> playerOptional = playerRepository.getPlayerByName(newPlayer.getUsername().toLowerCase());
        if (playerOptional.isPresent()){
            Player foundPlayer = playerOptional.get();
            return foundPlayer;
        } else {
            Player createdPlayer = playerRepository.createPlayer(newPlayer);
            session.setAttribute("username", createdPlayer.getUsername());
            return createdPlayer;
        }        
    }
}
