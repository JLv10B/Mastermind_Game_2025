package com.jl.mastermind.Controllers;

import org.springframework.web.bind.annotation.RestController;

import com.jl.mastermind.Models.Player;
import com.jl.mastermind.Models.PlayerGuess;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class APIController {

    @RequestMapping("/player")
    public Player viewPlayer() {
        Player player = new Player("Timmy");
        return player;
    }
    
    
    @RequestMapping("/feedback")
    public PlayerGuess feedback() {
        Player player = new Player("Timmy");
        return new PlayerGuess(player,"1234");
    }
    
}
