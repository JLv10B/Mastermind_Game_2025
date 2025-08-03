package com.jl.mastermind.Controllers;

import java.net.URISyntaxException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jl.mastermind.GameService;


@Controller
public class GameController {
    @RequestMapping("/")
    public String home(Model model) throws URISyntaxException {
        model.addAttribute("name", GameService.randomPatternGenerator());
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
