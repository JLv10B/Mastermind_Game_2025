package com.jl.mastermind.Models;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoomUpdate {
    private Integer difficulty;
    private Integer maxGuesses;
    private Boolean closed;
    private Boolean started;
    private Boolean mastercode;
    
    public RoomUpdate(Integer difficulty, Integer maxGuesses, Boolean closed, Boolean started,
            Boolean mastercode) {
        this.difficulty = difficulty;
        this.maxGuesses = maxGuesses;
        this.closed = closed;
        this.started = started;
        this.mastercode = mastercode;
    }
    
}
