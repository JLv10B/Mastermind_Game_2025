package com.jl.mastermind.Entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {
    private int roomId;
    private Player host;
    private int difficulty;
    private int maxGuesses;
    private boolean closed;
    private boolean started;
    private String mastercode;
    private Map<Player, List<PlayerGuess>> participants;
    
    public Room(int roomId, Player host, int difficulty, int maxGuesses, boolean closed, boolean started,
            String mastercode, Map<Player, List<PlayerGuess>> participants) {
        this.roomId = roomId;
        this.host = host;
        this.difficulty = difficulty;
        this.maxGuesses = maxGuesses;
        this.closed = closed;
        this.started = started;
        this.mastercode = mastercode;
        this.participants = participants;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getMaxGuesses() {
        return maxGuesses;
    }

    public void setMaxGuesses(int maxGuesses) {
        this.maxGuesses = maxGuesses;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public String getMastercode() {
        return mastercode;
    }

    public void setMastercode(String mastercode) {
        this.mastercode = mastercode;
    }

    public Map<Player, List<PlayerGuess>> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<Player, List<PlayerGuess>> participants) {
        this.participants = participants;
    }

    
}
