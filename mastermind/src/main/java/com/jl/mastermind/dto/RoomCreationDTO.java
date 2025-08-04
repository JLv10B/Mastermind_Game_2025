package com.jl.mastermind.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoomCreationDTO {
    @NotNull
    @Size(max=100)
    @JsonProperty("room_name")
    private String roomName;

    @NotNull
    @Min(value = 4, message = "Difficulty must be at least 4")
    @Max(value = 6, message = "Difficulty can't be higher than 6")
    private int difficulty;

    @JsonProperty("max_guesses")
    private int maxGuesses;
    private boolean closed;
    
    public RoomCreationDTO(String roomName, int difficulty, int maxGuesses, boolean closed,
            String mastercode) {
        this.roomName = roomName;
        this.difficulty = difficulty;
        this.maxGuesses = maxGuesses;
        this.closed = closed;
    }
    
}
