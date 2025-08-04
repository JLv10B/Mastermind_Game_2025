package com.jl.mastermind.Models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Player {
    @NotNull
    @Size(max=25)
    private String username;

    public Player(String username) {
        this.username = username;
    }

}
