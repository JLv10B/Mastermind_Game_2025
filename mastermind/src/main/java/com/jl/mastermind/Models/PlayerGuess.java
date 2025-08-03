package com.jl.mastermind.Models;


public class PlayerGuess {
    private int guessNumber;
    private Player player;
    private String guess;
    
    public PlayerGuess(Player player, String guess) {
        this.player = player;
        this.guess = guess;

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }


}
