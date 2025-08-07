package com.jl.mastermind.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import static com.jl.mastermind.util.AppConstants.RoomParameters.*;

@Data
public class RoomUpdateDTO {
    @Min(value = MIN_DIFFICULTY, message = "Difficulty must be at least " + MIN_DIFFICULTY)
    @Max(value = MAX_DIFFICULTY, message = "Difficulty can't be higher than " + MAX_DIFFICULTY)
    private Integer difficulty;

    private Boolean started;
    private Boolean resetMastercode;
    
    public RoomUpdateDTO(Integer difficulty, Boolean started, Boolean resetMastercode) {
        this.difficulty = difficulty;
        this.started = started;
        this.resetMastercode = resetMastercode;
    }
    
}
