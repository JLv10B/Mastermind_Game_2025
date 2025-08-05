package com.jl.mastermind.exceptions;

public class GameNotStartedException extends RuntimeException {
    public GameNotStartedException(String message) {
        super(message);
    }
}
