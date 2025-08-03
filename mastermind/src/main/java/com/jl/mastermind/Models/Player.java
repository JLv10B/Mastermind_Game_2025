package com.jl.mastermind.Models;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Player {
    @NotNull
    @Size(max=25)
    private String username;

    public Player(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
