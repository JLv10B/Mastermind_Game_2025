package com.jl.mastermind.Controllers;

import java.util.Map;
import java.util.Optional;

import javax.naming.NameNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jl.mastermind.Exceptions.ResourceNotFoundException;
import com.jl.mastermind.Models.Player;
import com.jl.mastermind.Repositories.PlayerRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @GetMapping()
    public Map<String, Player> getAllPlayers() {
        return playerRepository.getPlayerMap();
    }

    @GetMapping("/{username}")
    public ResponseEntity<Player> getPlayerByName(@PathVariable String username) {
       return playerRepository.getPlayerByName(username.toLowerCase()).map(ResponseEntity::ok).orElseThrow(() -> new ResourceNotFoundException(username + " not found"));
    }

    @PostMapping("/create-player")
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody Player newPlayer) {
        
        
        Player createdPlayer = playerRepository.createPlayer(newPlayer);
        if (createdPlayer == null) {
            return ResponseEntity.internalServerError().build();
        } 
        else {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer);
        }
    }
}
