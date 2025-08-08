package com.jl.mastermind.entities;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Player implements Serializable {
    @NotNull
    @Size(min=3, max=25)
    private String username;

    public Player(String username) {
        this.username = username;
    }

}
