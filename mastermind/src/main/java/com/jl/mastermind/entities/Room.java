package com.jl.mastermind.entities;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Room {
    @NotNull
    @Size(max=100)
    @JsonProperty("room_name")
    private String roomName;

    @NotNull
    private Player host;

    @NotNull
    @Min(value = 4, message = "Difficulty must be at least 4")
    @Max(value = 6, message = "Difficulty can't be higher than 6")
    private int difficulty;

    @JsonProperty("max_guesses")
    private int maxGuesses;
    private boolean closed;
    private boolean started;

    private String mastercode;
    private Map<String, List<PlayerGuess>> participants;
    
    public Room(String roomName, Player host, int difficulty, int maxGuesses, boolean closed, boolean started,
            String mastercode, Map<String, List<PlayerGuess>> participants) {
        this.roomName = roomName;
        this.host = host;
        this.difficulty = difficulty;
        this.maxGuesses = maxGuesses;
        this.closed = closed;
        this.started = started;
        this.mastercode = mastercode;
        this.participants = participants;
    }
    
}
