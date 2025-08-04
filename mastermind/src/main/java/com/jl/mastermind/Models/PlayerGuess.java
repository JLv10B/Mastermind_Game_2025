package com.jl.mastermind.Models;

import lombok.Data;

@Data
public class PlayerGuess {
    private int guessNumber;
    private Player player;
    private String guess;
    
    public PlayerGuess(Player player, String guess) {
        this.player = player;
        this.guess = guess;

    }


}
