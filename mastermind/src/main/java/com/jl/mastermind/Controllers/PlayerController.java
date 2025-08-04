package com.jl.mastermind.Controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jl.mastermind.Exceptions.ResourceNotFoundException;
import com.jl.mastermind.Exceptions.UsernameAlreadyExistsException;
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
       Optional<Player> playerOptional = playerRepository.getPlayerByName(username.toLowerCase());
       if (playerOptional.isPresent()) {
        Player player = playerOptional.get();
        return ResponseEntity.ok(player);
       } else {
        throw new ResourceNotFoundException(username + " not found");
       }
    }


    @DeleteMapping("/{username}")
    public ResponseEntity<String> deletePlayer(@PathVariable String username) {
        boolean deleted = false;
        Optional<Player> playerOptional = playerRepository.getPlayerByName(username.toLowerCase());
        if (playerOptional.isPresent()) {
            deleted = playerRepository.deletePlayer(username.toLowerCase());
        } else {
            throw new ResourceNotFoundException(username + " not found");
        }
        if (deleted == true) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("/create-player")
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody Player newPlayer) {
        Optional<Player> playerOptional = playerRepository.getPlayerByName(newPlayer.getUsername().toLowerCase());
        if (playerOptional.isPresent()){
            throw new UsernameAlreadyExistsException(newPlayer.getUsername() + " already exists");
        } else {
            playerRepository.createPlayer(newPlayer);
            playerOptional = playerRepository.getPlayerByName(newPlayer.getUsername().toLowerCase());
            if (playerOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(newPlayer);
            } else {
                return ResponseEntity.internalServerError().build();
            }
        }
    }
}
