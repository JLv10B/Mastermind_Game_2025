package com.jl.mastermind.entities;

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
