package com.jl.mastermind.Entities;

public class PlayerGuess {
    private Player player;
    private String guess;
    private int matchingNumbers;
    private int exactMatches;
    
    public PlayerGuess(Player player, String guess, int matchingNumbers, int exactMatches) {
        this.player = player;
        this.guess = guess;
        this.matchingNumbers = matchingNumbers;
        this.exactMatches = exactMatches;
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

    public int getMatchingNumbers() {
        return matchingNumbers;
    }

    public void setMatchingNumbers(int matchingNumbers) {
        this.matchingNumbers = matchingNumbers;
    }

    public int getExactMatches() {
        return exactMatches;
    }

    public void setExactMatches(int exactMatches) {
        this.exactMatches = exactMatches;
    }


}
