package com.jl.mastermind.controller;


import com.jl.mastermind.controllers.PlayerScoreController;
import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.services.PlayerScoreService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerScoreController.class)
@ActiveProfiles("dev")
class PlayerScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerScoreService playerScoreService;

    private List<PlayerScore> mockPlayerScoresEasy;
    private List<PlayerScore> mockPlayerScoresMedium;
    private List<PlayerScore> mockPlayerScoresHard;

    @BeforeEach
    void setUp() {
        this.mockPlayerScoresEasy = List.of(
            PlayerScore.builder().username("testuser1").score(1).difficulty(4).build(),
            PlayerScore.builder().username("testuser2").score(4).difficulty(4).build(),
            PlayerScore.builder().username("testuser3").score(7).difficulty(4).build(),
            PlayerScore.builder().username("testuser4").score(10).difficulty(4).build(),
            PlayerScore.builder().username("testuser5").score(13).difficulty(4).build()
            );
            
        this.mockPlayerScoresMedium = List.of(
            PlayerScore.builder().username("testuser1").score(2).difficulty(5).build(),
            PlayerScore.builder().username("testuser2").score(5).difficulty(5).build(),
            PlayerScore.builder().username("testuser3").score(8).difficulty(5).build(),
            PlayerScore.builder().username("testuser4").score(11).difficulty(5).build(),
            PlayerScore.builder().username("testuser5").score(14).difficulty(5).build()
            );
                
        this.mockPlayerScoresHard = List.of(
            PlayerScore.builder().username("testuser1").score(3).difficulty(6).build(),
            PlayerScore.builder().username("testuser2").score(6).difficulty(6).build(),
            PlayerScore.builder().username("testuser3").score(9).difficulty(6).build(),
            PlayerScore.builder().username("testuser4").score(12).difficulty(6).build(),
            PlayerScore.builder().username("testuser5").score(15).difficulty(6).build()
            );
    }

    @Test
    void testGetTop10Entity_ScoresExistEasy() throws Exception {
        when(playerScoreService.getTop10("4")).thenReturn(mockPlayerScoresEasy);

        mockMvc.perform(get("/scoreleaderboard/difficulty/4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void testGetTop10Entity_ScoresExistMedium() throws Exception {
        when(playerScoreService.getTop10("5")).thenReturn(mockPlayerScoresMedium);

        mockMvc.perform(get("/scoreleaderboard/difficulty/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void testGetTop10Entity_ScoresExistHard() throws Exception {
        when(playerScoreService.getTop10("6")).thenReturn(mockPlayerScoresHard);

        mockMvc.perform(get("/scoreleaderboard/difficulty/6"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void testGetPlayerScores_ScoresExist() throws Exception {
        List<PlayerScore> mockList = List.of(
            PlayerScore.builder().username("testuser1").score(1).difficulty(4).build(),
            PlayerScore.builder().username("testuser1").score(2).difficulty(5).build(),
            PlayerScore.builder().username("testuser1").score(3).difficulty(6).build()
        );
        when(playerScoreService.getPlayerScores("testuser1")).thenReturn(mockList);

        mockMvc.perform(get("/scoreleaderboard/username/testuser1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));
    }
}