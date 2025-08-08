package com.jl.mastermind.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jl.mastermind.controllers.PlayerController;
import com.jl.mastermind.entities.Player;
import com.jl.mastermind.services.PlayerService;


@WebMvcTest(PlayerController.class)
@ActiveProfiles("dev")
public class PlayerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private PlayerService playerService;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    private MockHttpSession mockHttpSession;
    private Player mockPlayer;


    @BeforeEach
    void setUp() {
        this.mockPlayer = new Player("testuser");
        this.mockHttpSession = new MockHttpSession();
    }

    @Test
    void testGetPlayerByName_Exists() throws Exception {
        String username = "testuser";
        when(playerService.getPlayerByName(username.toLowerCase())).thenReturn(mockPlayer);

        mockMvc.perform(get("/players/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void testDeletePlayer_Exists() throws Exception {
        String username = "testuser";
        when(playerService.deletePlayer(username.toLowerCase())).thenReturn(true);

        mockMvc.perform(delete("/players/testuser"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeletePlayer_DoesNotExist() throws Exception {
        String username = "testuser";
        when(playerService.deletePlayer(username.toLowerCase())).thenReturn(false);

        mockMvc.perform(delete("/players/testuser"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetOrCreatePlayer() throws Exception {
        String username = "testuser";
        when(playerService.getOrCreatePlayer(mockPlayer, mockHttpSession)).thenReturn(mockPlayer);

        mockMvc.perform(post("/players/create-player")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockPlayer)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is(username)));
    }  

}
