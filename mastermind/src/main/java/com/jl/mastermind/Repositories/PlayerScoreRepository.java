package com.jl.mastermind.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jl.mastermind.entities.PlayerScore;

@Repository
public interface PlayerScoreRepository extends JpaRepository<PlayerScore, String> {

    List<PlayerScore> findByUsername(String username);
    Optional<PlayerScore> findByUsernameAndDifficulty(String username, int difficulty);
    List<PlayerScore> findTop10ByDifficultyOrderByScoreDesc(int difficulty);
}