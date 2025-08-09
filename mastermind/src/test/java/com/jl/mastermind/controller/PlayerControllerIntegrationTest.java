package com.jl.mastermind.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jl.mastermind.entities.Player;
import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.repositories.PlayerRepository;
import com.jl.mastermind.repositories.PlayerScoreRepository;
import com.jl.mastermind.services.PlayerScoreService;
import com.jl.mastermind.services.PlayerService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class PlayerControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private PlayerScoreRepository playerScoreRepository;

        @Autowired
        private PlayerRepository playerRepository;

        private MockHttpSession mockHttpSession;
        private ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        void setUp(){
                playerScoreRepository.deleteAll();
                playerRepository.deleteAll();
                this.mockHttpSession = new MockHttpSession();
        }

        @Test
        void testCreatePlayer_NewPlayer() throws Exception {
                String username = "testuser";
                Player newPlayer = new Player(username);
                mockMvc.perform(post("/players/create-player")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPlayer)))
                        .andExpect(status().isCreated());

                Optional<Player> foundPlayerOptional = playerRepository.getPlayerByName(username);
                Player foundPlayer = foundPlayerOptional.get();
                List<PlayerScore> playerScoreList = playerScoreRepository.findByUsername(username);

                assertEquals(username, foundPlayer.getUsername());
                assertEquals(3, playerScoreList.size());
        }
}
