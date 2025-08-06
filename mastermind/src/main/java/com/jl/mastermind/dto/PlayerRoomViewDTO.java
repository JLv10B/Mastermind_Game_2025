package com.jl.mastermind.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jl.mastermind.entities.Player;
import com.jl.mastermind.entities.PlayerGuess;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static com.jl.mastermind.util.AppConstants.RoomParameters.*;

@Data
public class PlayerRoomViewDTO {
    @NotNull
    @Size(min=MIN_ROOM_NAME_SIZE, max=MAX_ROOM_NAME_SIZE)
    @JsonProperty("room_name")
    private String roomName;

    @NotNull
    private Player host;

    @NotNull
    @Min(value = MIN_DIFFICULTY, message = "Difficulty must be at least " + MIN_DIFFICULTY)
    @Max(value = MAX_DIFFICULTY, message = "Difficulty can't be higher than " + MAX_DIFFICULTY)
    private int difficulty;

    @JsonProperty("max_guesses")
    private int maxGuesses;
    private boolean closed;
    private boolean started;
    private boolean completed;
    private List<PlayerGuess> guessList;
    
    public PlayerRoomViewDTO(String roomName, Player host, int difficulty, int maxGuesses, boolean closed, boolean started, boolean completed, List<PlayerGuess> guessList) {
        this.roomName = roomName;
        this.host = host;
        this.difficulty = difficulty;
        this.maxGuesses = maxGuesses;
        this.closed = closed;
        this.started = started;
        this.completed = completed;
        this.guessList = guessList;
    }
    
}