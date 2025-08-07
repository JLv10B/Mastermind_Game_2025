// package com.jl.mastermind.controllers;

// import java.util.List;
// import java.util.stream.StreamSupport;

// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// import com.jl.mastermind.entities.PlayerScore;
// import com.jl.mastermind.services.PlayerScoreService;

// @Controller
// @RequestMapping("/ScoreLeaderboard")
// public class PlayerScoreController {
//     private final PlayerScoreService playerScoreService;

//     public PlayerScoreController(PlayerScoreService playerScoreService){
//         this.playerScoreService = playerScoreService;
//     }

//     @GetMapping
//     public ResponseEntity<List<PlayerScore>> getTop10Entity() {
//         Iterable<PlayerScore> allScoresIterable = playerScoreService.getTop10();
//         List<PlayerScore> allScoresList = StreamSupport.stream(allScoresIterable.spliterator(), false).toList();
//         return ResponseEntity.ok(allScoresList);
//     }
// }
