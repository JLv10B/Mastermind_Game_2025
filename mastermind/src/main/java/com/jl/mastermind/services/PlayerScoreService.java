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

    /**
     * Retrieves all scores for a specific player, ordered by difficulty.
     * 
     * @param username the username of the player whose scores to retrieve
     * @return a List of PlayerScore objects ordered by difficulty level
     * @throws ResourceNotFoundException if no scores are found for the specified username
     */
    public List<PlayerScore> getPlayerScores(String username) {
        List<PlayerScore> playerScores = playerScoreRepository.findByUsernameOrderByDifficulty(username);
        if (playerScores.isEmpty()) {
            throw new ResourceNotFoundException("No scores found");
        }
        return playerScores;
    }

    /**
     * Retrieves the top 10 highest scores for a specific difficulty level.
     * 
     * @param difficulty the difficulty level as a String (will be parsed to int)
     * @return a List of the top 10 PlayerScore objects ordered by score in descending order
     * @throws ResourceNotFoundException if no scores are found for the specified difficulty
     * @throws NumberFormatException if the difficulty parameter cannot be parsed as an integer
     */
    public List<PlayerScore> getTop10(String difficulty) {
        int difficultyInt = Integer.parseInt(difficulty);
        List<PlayerScore> playerScores = playerScoreRepository.findTop10ByDifficultyOrderByScoreDesc(difficultyInt);
        if (playerScores.isEmpty()) {
            throw new ResourceNotFoundException("No scores found");
        }
        return playerScores;
    }

    /**
     * Creates initial score entries for a new player across all difficulty levels.
     * 
     * <p>This method creates PlayerScore entries with default values (score of 0)
     * for difficulty levels 4, 5, and 6. If the player already has scores,
     * no action is taken to avoid duplicates.</p>
     * 
     * <p><strong>Note:</strong> This method has an early return and does not throw
     * exceptions if scores already exist.</p>
     * 
     * @param player the Player object for whom to create initial scores
     */
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
    
    /**
     * Updates a player's score by incrementing it by 1 for a specific difficulty level.
     * 
     * <p>This method retrieves the existing score for the specified username and difficulty,
     * increments the score by 1, and saves the updated score back to the repository.</p>
     * 
     * @param username the username of the player whose score should be updated
     * @param difficulty the difficulty level for which the score should be updated
     * @return the updated PlayerScore object with the incremented score
     * @throws ResourceNotFoundException if no score is found for the specified username and difficulty combination
     */
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
