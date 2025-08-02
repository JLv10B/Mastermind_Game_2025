package com.jl.mastermind;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class GameController {
    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("name", "Timmy");
        return "index.html";
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
