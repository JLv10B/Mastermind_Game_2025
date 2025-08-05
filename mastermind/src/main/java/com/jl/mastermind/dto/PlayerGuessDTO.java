package com.jl.mastermind.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlayerGuessDTO {
    @NotNull
    private String playerGuess;

    public PlayerGuessDTO(String playerGuess) {
        this.playerGuess = playerGuess;
    }
}
