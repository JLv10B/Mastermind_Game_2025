package com.jl.mastermind.controller;


import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.services.PlayerScoreService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class PlayerScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerScoreService playerScoreService;

    private List<PlayerScore> mockPlayerScores;

    @BeforeEach
    void setUp() {
        this.mockPlayerScores = List.of(
            PlayerScore.builder().username("testuser1").score(1).difficulty(4).build(),
            PlayerScore.builder().username("testuser2").score(2).difficulty(4).build(),
            PlayerScore.builder().username("testuser3").score(3).difficulty(4).build(),
            PlayerScore.builder().username("testuser4").score(4).difficulty(4).build(),
            PlayerScore.builder().username("testuser5").score(5).difficulty(4).build(),
            PlayerScore.builder().username("testuser6").score(6).difficulty(4).build(),
            PlayerScore.builder().username("testuser7").score(7).difficulty(4).build(),
            PlayerScore.builder().username("testuser8").score(8).difficulty(4).build(),
            PlayerScore.builder().username("testuser9").score(9).difficulty(4).build(),
            PlayerScore.builder().username("testuser10").score(10).difficulty(4).build(),
            PlayerScore.builder().username("testuser11").score(11).difficulty(4).build(),
            PlayerScore.builder().username("testuser12").score(12).difficulty(4).build(),
            PlayerScore.builder().username("testuser13").score(13).difficulty(4).build(),
            PlayerScore.builder().username("testuser14").score(14).difficulty(4).build(),
            PlayerScore.builder().username("testuser15").score(15).difficulty(4).build()
        );
    }

    @Test
    void getTop10Entity_ShouldReturnTop10Scores_WhenScoresExist() throws Exception {

        when(playerScoreService.getTop10("4")).thenReturn(mockPlayerScores.subList(5, 15));

        mockMvc.perform(get("/ScoreLeaderboard/difficulty/4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(10));
    }

    @Test
    void getTop10Entity_ShouldReturnEmptyList_WhenNoScoresExist() throws Exception {

        when(playerScoreService.getTop10("4")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/ScoreLeaderboard/difficulty/4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(content().json("[]"));
    }

}