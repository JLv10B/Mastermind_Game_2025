package com.jl.mastermind.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RoomUpdateDTO {
    private Integer difficulty;
    
    @JsonProperty("max_guesses")
    private Integer maxGuesses;
    private Boolean closed;
    private Boolean started;
    private Boolean mastercode;
    
    public RoomUpdateDTO(Integer difficulty, Integer maxGuesses, Boolean closed, Boolean started,
            Boolean mastercode) {
        this.difficulty = difficulty;
        this.maxGuesses = maxGuesses;
        this.closed = closed;
        this.started = started;
        this.mastercode = mastercode;
    }
    
}
