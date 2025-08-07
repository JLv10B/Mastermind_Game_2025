package com.jl.mastermind.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.repositories.PlayerScoreRepository;

@Service
public class PlayerScoreService {
    private final PlayerScoreRepository playerScoreRepository;

    public PlayerScoreService(PlayerScoreRepository playerScoreRepository) {
        this.playerScoreRepository = playerScoreRepository;
    }

    // public List<PlayerScore> getTop10() {
    //     return playerScoreRepository.findByTop10ByOrderByScoreDesc();
    // }

}
