package com.jl.mastermind.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class GameController {
    @RequestMapping("/")
    public String home() {
        return "welcome-page";
    }

    @RequestMapping("/room-creation")
    public String roomCreation() {
        return "room-creation-page";
    }
    @RequestMapping("/game-room")
    public String gameRoom() {
        return "game-room-page";
    }
    
}
