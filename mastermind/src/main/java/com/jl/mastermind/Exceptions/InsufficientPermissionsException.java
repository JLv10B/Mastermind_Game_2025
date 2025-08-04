package com.jl.mastermind.exceptions;

public class InsufficientPermissionsException extends RuntimeException{
    public InsufficientPermissionsException(String message) {
        super(message);
    }
}
