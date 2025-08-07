package com.jl.mastermind.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jl.mastermind.entities.PlayerScore;

@Repository
public interface PlayerScoreRepository extends JpaRepository<PlayerScore, String> {

    // List<PlayerScore> findByTop10ByOrderByScoreDesc();
}