package com.jl.mastermind.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static com.jl.mastermind.util.AppConstants.RoomParameters.*;
@Data
public class RoomCreationDTO {
    @NotNull
    @Size(max=100)
    @JsonProperty("room_name")
    private String roomName;

    @NotNull
    @Min(value = MIN_DIFFICULTY, message = "Difficulty must be at least " + MIN_DIFFICULTY)
    @Max(value = MAX_DIFFICULTY, message = "Difficulty can't be higher than " + MAX_DIFFICULTY)
    private Integer difficulty;
    
    private boolean closed;
    
    public RoomCreationDTO(String roomName, int difficulty) {
        this.roomName = roomName;
        this.difficulty = difficulty;
    }
    
}
