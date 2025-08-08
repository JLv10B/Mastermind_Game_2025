package com.jl.mastermind.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class GameController {
    @RequestMapping("/")
    public String home() {
        return "index";
    }
    
}
