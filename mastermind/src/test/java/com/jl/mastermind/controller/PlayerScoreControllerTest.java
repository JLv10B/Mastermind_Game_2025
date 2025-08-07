// package com.jl.mastermind.controller;


// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.jl.mastermind.controllers.PlayerScoreController;
// import com.jl.mastermind.entities.PlayerScore;
// import com.jl.mastermind.services.PlayerScoreService;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;

// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;

// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(controllers = PlayerScoreController.class)
// class PlayerScoreControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockitoBean
//     private PlayerScoreService playerScoreService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     private List<PlayerScore> mockPlayerScores;

//     @BeforeEach
//     void setUp() {
//         // Create mock data - adjust based on your PlayerScore class structure
//         mockPlayerScores = Arrays.asList(
//             new PlayerScore("Player1", 1000, 4),
//             new PlayerScore("Player2", 950, 4),
//             new PlayerScore("Player3", 900, 4),
//             new PlayerScore("Player4", 850, 4),
//             new PlayerScore("Player5", 800, 4)
//         );
//     }

//     @Test
//     void getTop10Entity_ShouldReturnTop10Scores_WhenScoresExist() throws Exception {
//         // Given
//         when(playerScoreService.getTop10()).thenReturn(mockPlayerScores);

//         // When & Then
//         mockMvc.perform(get("/ScoreLeaderboard"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.length()").value(5))
//                 .andExpect(jsonPath("$[0].playerName").value("Player1"))
//                 .andExpect(jsonPath("$[0].score").value(1000))
//                 .andExpect(jsonPath("$[1].playerName").value("Player2"))
//                 .andExpect(jsonPath("$[1].score").value(950));
//     }

//     @Test
//     void getTop10Entity_ShouldReturnEmptyList_WhenNoScoresExist() throws Exception {
//         // Given
//         when(playerScoreService.getTop10()).thenReturn(Collections.emptyList());

//         // When & Then
//         mockMvc.perform(get("/ScoreLeaderboard"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.length()").value(0))
//                 .andExpect(content().json("[]"));
//     }

//     @Test
//     void getTop10Entity_ShouldReturnExactly10Scores_WhenMoreThan10ScoresExist() throws Exception {
//         // Given - Create 15 mock scores
//         List<PlayerScore> fifteenScores = Arrays.asList(
//             new PlayerScore("Player1", 1000, 4),
//             new PlayerScore("Player2", 950, 4),
//             new PlayerScore("Player3", 900, 4),
//             new PlayerScore("Player4", 850, 4),
//             new PlayerScore("Player5", 800, 4),
//             new PlayerScore("Player6", 750, 4),
//             new PlayerScore("Player7", 700, 4),
//             new PlayerScore("Player8", 650, 4),
//             new PlayerScore("Player9", 600, 4),
//             new PlayerScore("Player10", 550, 4),
//             new PlayerScore("Player11", 500, 4),
//             new PlayerScore("Player12", 450, 4),
//             new PlayerScore("Player13", 400, 4),
//             new PlayerScore("Player14", 350, 4),
//             new PlayerScore("Player15", 300, 4)
//         );

//         when(playerScoreService.getTop10()).thenReturn(fifteenScores.subList(0, 10));

//         // When & Then
//         mockMvc.perform(get("/ScoreLeaderboard"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.length()").value(10));
//     }

//     @Test
//     void getTop10Entity_ShouldHandleServiceException() throws Exception {
//         // Given
//         when(playerScoreService.getTop10()).thenThrow(new RuntimeException("Database error"));

//         // When & Then
//         mockMvc.perform(get("/ScoreLeaderboard"))
//                 .andExpect(status().is5xxServerError());
//     }
// }