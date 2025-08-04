package com.jl.mastermind.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class GameController {
    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping("/create-game")
    public String createGame() {
        return "create_game.html";
    }

    @RequestMapping("/game-page")
    public String gamePage() {
        return "game_page.html";
    }
    
}
