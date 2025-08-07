package com.jl.mastermind.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jl.mastermind.entities.ScoreLeaderboard;

@Repository
public interface ScoreLeaderboardRepository extends CrudRepository<ScoreLeaderboard, String> {
    

    
}