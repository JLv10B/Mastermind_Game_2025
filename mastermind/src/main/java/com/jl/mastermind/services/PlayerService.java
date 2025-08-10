package com.jl.mastermind.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jl.mastermind.exceptions.*;
import com.jl.mastermind.repositories.PlayerRepository;
import com.jl.mastermind.entities.Player;

import jakarta.servlet.http.HttpSession;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerScoreService playerScoreService;

    public PlayerService(PlayerRepository playerRepository, PlayerScoreService playerScoreService) {
        this.playerRepository = playerRepository;
        this.playerScoreService = playerScoreService;
    }

    /**
     * Retrieves all players from the repository.
     * 
     * @return a Map containing all players, where the key is the player identifier
     *         and the value is the Player object
     */
    public Map<String, Player> getAllPlayers() {
        return playerRepository.getPlayerMap();
    }
    
    /**
     * Retrieves a player by their username.
     * 
     * <p>The username lookup is case-insensitive as it's converted to lowercase
     * before querying the repository.</p>
     * 
     * @param username the username of the player to retrieve (case-insensitive)
     * @return the Player object associated with the given username
     * @throws ResourceNotFoundException if no player is found with the specified username
     */
    public Player getPlayerByName(String username) {
        Optional<Player> playerOptional = playerRepository.getPlayerByName(username.toLowerCase());
        if (playerOptional.isPresent()) {
            return playerOptional.get();
       } else {
            throw new ResourceNotFoundException(username + " not found");
       }
    }

    /**
     * Deletes a player by their username.
     * 
     * <p>The username lookup is case-insensitive as it's converted to lowercase
     * before querying the repository.</p>
     * 
     * @param username the username of the player to delete (case-insensitive)
     * @return {@code true} if the player was successfully deleted, {@code false} otherwise
     * @throws ResourceNotFoundException if no player is found with the specified username
     */
    public boolean deletePlayer(String username) {
        Optional<Player> playerOptional = playerRepository.getPlayerByName(username.toLowerCase());
        if (!playerOptional.isPresent()) {
            throw new ResourceNotFoundException(username + " not found");
        } else {
            return playerRepository.deletePlayer(username.toLowerCase());
        }
    }

    /**
     * Retrieves an existing player or creates a new one if not found.
     * 
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Searches for an existing player with the given username (case-insensitive)</li>
     *   <li>If found, returns the existing player and sets the session attribute</li>
     *   <li>If not found, creates a new player, initializes their scores, and sets the session attribute</li>
     * </ul>
     * 
     * <p>The username is stored in the HTTP session for subsequent requests.</p>
     * 
     * @param newPlayer the Player object containing the details for a potential new player
     * @param session the HTTP session to store the username attribute
     * @return the existing Player if found, or the newly created Player
     * @throws RuntimeException if player creation fails (propagated from repository or score service)
     */
    public Player getOrCreatePlayer(Player newPlayer, HttpSession session) {
        Optional<Player> playerOptional = playerRepository.getPlayerByName(newPlayer.getUsername().toLowerCase());
        if (playerOptional.isPresent()){
            Player foundPlayer = playerOptional.get();
            session.setAttribute("username", foundPlayer.getUsername());
            return foundPlayer;
        } else {
            Player createdPlayer = playerRepository.createPlayer(newPlayer);
            playerScoreService.createInitialPlayerScores(newPlayer);
            session.setAttribute("username", createdPlayer.getUsername());
            return createdPlayer;
        }        
    }
}
