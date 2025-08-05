package com.jl.mastermind.entities;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlayerGuess {
    @NotNull
    private String playerGuess;
    private int exactMatches;
    private String feedback;

    public PlayerGuess(String playerGuess, int exactMatches, String feedback) {
        this.playerGuess = playerGuess;
        this.exactMatches = exactMatches;
        this.feedback = feedback;
    }
}
