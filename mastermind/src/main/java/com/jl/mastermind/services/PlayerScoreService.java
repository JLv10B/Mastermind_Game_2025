package com.jl.mastermind.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jl.mastermind.entities.Player;
import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.exceptions.*;
import com.jl.mastermind.repositories.PlayerScoreRepository;

@Service
public class PlayerScoreService {
    private final PlayerScoreRepository playerScoreRepository;

    public PlayerScoreService(PlayerScoreRepository playerScoreRepository) {
        this.playerScoreRepository = playerScoreRepository;
    }

    public List<PlayerScore> getPlayerScores(String username) {
        List<PlayerScore> playerScores = playerScoreRepository.findByUsernameOrderByDifficulty(username);
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

    public void createInitialPlayerScores(Player player) {
        List<PlayerScore> playerScoreList = playerScoreRepository.findByUsername(player.getUsername());
        if (!playerScoreList.isEmpty()) {
            return;
        }
        playerScoreList = List.of(
            PlayerScore.builder().username(player.getUsername()).difficulty(4).build(),
            PlayerScore.builder().username(player.getUsername()).difficulty(5).build(),
            PlayerScore.builder().username(player.getUsername()).difficulty(6).build()
        );
        playerScoreRepository.saveAll(playerScoreList);
        return;
    }

    public PlayerScore updatePlayerScore(String username, int difficulty) {
        Optional<PlayerScore> updatedPlayerScoreOptional = playerScoreRepository.findByUsernameAndDifficulty(username, difficulty);
        if (!updatedPlayerScoreOptional.isPresent()) {
            throw new ResourceNotFoundException("Score not found");
        }
        PlayerScore updatedPlayerScore = updatedPlayerScoreOptional.get();
        updatedPlayerScore.setScore(updatedPlayerScore.getScore() + 1);
        return playerScoreRepository.save(updatedPlayerScore);

    }

}
