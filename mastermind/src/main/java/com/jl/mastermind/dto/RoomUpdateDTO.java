package com.jl.mastermind.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import static com.jl.mastermind.util.AppConstants.RoomParameters.*;

@Data
public class RoomUpdateDTO {
    @Min(value = MIN_DIFFICULTY, message = "Difficulty must be at least " + MIN_DIFFICULTY)
    @Max(value = MAX_DIFFICULTY, message = "Difficulty can't be higher than " + MAX_DIFFICULTY)
    private Integer difficulty;
    
    @JsonProperty("max_guesses")
    @Min(value = MIN_GUESSES, message = "You must have a minimum of " + MIN_GUESSES + " guesses")
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
