package com.jl.mastermind;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

/**
 * GameService handles all game logic such as generating a randomized mastercode, 
 */
@Service
public class GameService {
    private String masterCode;
    private final int CODE_LENGTH = 4;

    public GameService() {
        // this.masterCode = randomPatternGenerator();
    }

    // public String randomPatternGenerator() {
        
    // }

    public String getMasterCode() {
        return masterCode;
    }
}
