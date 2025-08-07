package com.jl.mastermind.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jl.mastermind.entities.Player;
import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.exceptions.NameAlreadyExistsException;
import com.jl.mastermind.exceptions.ResourceNotFoundException;
import com.jl.mastermind.repositories.PlayerScoreRepository;

@Service
public class PlayerScoreService {
    private final PlayerScoreRepository playerScoreRepository;

    public PlayerScoreService(PlayerScoreRepository playerScoreRepository) {
        this.playerScoreRepository = playerScoreRepository;
    }

    public List<PlayerScore> getPlayerScores(String username) {
        List<PlayerScore> playerScores = playerScoreRepository.findByUsername(username);
        if (playerScores.isEmpty()) {
            throw new ResourceNotFoundException("No scores found");
        }
        return playerScores;
    }

    public List<PlayerScore> getTop10(String difficulty) {
        int difficultyInt = Integer.parseInt(difficulty);
        List<PlayerScore> playerScores = playerScoreRepository.findTop10ByDifficultyOrderByScoreDesc(difficultyInt);
        if (playerScores.isEmpty()) {
            throw new ResourceNotFoundException("No scores found");
        }
        return playerScores;
    }

    public List<PlayerScore> createInitialPlayerScores(Player player) {
        List<PlayerScore> playerScoreList = playerScoreRepository.findByUsername(player.getUsername());
        if (!playerScoreList.isEmpty()) {
            throw new NameAlreadyExistsException("Username already being used");
        }
        PlayerScore easyPlayerScore = new PlayerScore(player.getUsername(), 0, 4);
        PlayerScore mediumPlayerScore = new PlayerScore(player.getUsername(), 0, 5);
        PlayerScore hardPlayerScore = new PlayerScore(player.getUsername(), 0, 6);
        playerScoreList.add(easyPlayerScore);
        playerScoreList.add(mediumPlayerScore);
        playerScoreList.add(hardPlayerScore);
        return playerScoreRepository.saveAll(playerScoreList);
    }

    public PlayerScore updatPlayerScore(PlayerScore playerScore) {
        Optional<PlayerScore> updatedPlayerScoreOptional = playerScoreRepository.findByUsernameAndDifficulty(playerScore.getUsername(), playerScore.getDifficulty());
        if (!updatedPlayerScoreOptional.isPresent()) {
            throw new ResourceNotFoundException("Score not found");
        }
        PlayerScore updatedPlayerScore = updatedPlayerScoreOptional.get();
        updatedPlayerScore.setScore(updatedPlayerScore.getScore() + 1);
        return playerScoreRepository.save(updatedPlayerScore);

    }

}
