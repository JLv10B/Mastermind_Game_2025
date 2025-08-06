package com.jl.mastermind.entities;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlayerGuess {
    @NotNull
    private String playerGuess;
    private boolean correctGuess;
    private String feedback;
    private Integer remainingGuesses;

    public PlayerGuess(String playerGuess, boolean correctGuess, String feedback, int remainingGuesses) {
        this.playerGuess = playerGuess;
        this.correctGuess = correctGuess;
        this.feedback = feedback;
        this.remainingGuesses = remainingGuesses;
    }
}
