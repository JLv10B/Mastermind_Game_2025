package com.jl.mastermind.Models;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Room {
    @NotNull
    @Size(max=100)
    private String roomName;

    @NotNull
    private Player host;

    @NotNull
    private int difficulty;
    private int maxGuesses;
    private boolean closed;
    private boolean started;

    @NotNull
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
