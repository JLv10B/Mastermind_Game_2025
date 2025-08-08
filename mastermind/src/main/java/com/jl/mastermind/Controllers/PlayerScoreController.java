package com.jl.mastermind.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.services.PlayerScoreService;

@Controller
@RequestMapping("/scoreleaderboard")
public class PlayerScoreController {
    private final PlayerScoreService playerScoreService;

    public PlayerScoreController(PlayerScoreService playerScoreService){
        this.playerScoreService = playerScoreService;
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<PlayerScore>> getTop10Entity(@PathVariable String difficulty) {
        List<PlayerScore> allScores = playerScoreService.getTop10(difficulty);
        return ResponseEntity.ok(allScores);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<List<PlayerScore>> getPlayerScores(@PathVariable String username) {
        List<PlayerScore> playerScores = playerScoreService.getPlayerScores(username);
        return ResponseEntity.ok(playerScores);
    }
}
