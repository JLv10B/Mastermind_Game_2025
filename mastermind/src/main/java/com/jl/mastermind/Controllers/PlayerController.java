package com.jl.mastermind.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jl.mastermind.services.PlayerService;
import com.jl.mastermind.entities.Player;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService playerService;

    
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }


    @GetMapping()
    public Map<String, Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }


    @GetMapping("/{username}")
    public ResponseEntity<Player> getPlayerByName(@PathVariable String username) {
        Player player = playerService.getPlayerByName(username.toLowerCase());
        return ResponseEntity.ok(player);
    }


    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deletePlayer(@PathVariable String username) {
        Boolean deleted = playerService.deletePlayer(username.toLowerCase());
        if (deleted == true) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("/create-player")
    public ResponseEntity<Player> getOrCreatePlayer(@Valid @RequestBody Player newPlayer, HttpSession session) {
        Player createdPlayer = playerService.getOrCreatePlayer(newPlayer, session);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer);
    }
}
