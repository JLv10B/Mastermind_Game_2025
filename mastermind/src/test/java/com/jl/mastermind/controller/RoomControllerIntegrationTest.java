package com.jl.mastermind.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jl.mastermind.dto.RoomCreationDTO;
import com.jl.mastermind.entities.Player;
import com.jl.mastermind.entities.PlayerGuess;
import com.jl.mastermind.entities.Room;
import com.jl.mastermind.repositories.PlayerRepository;
import com.jl.mastermind.repositories.PlayerScoreRepository;
import com.jl.mastermind.repositories.RoomRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class RoomControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private PlayerScoreRepository playerScoreRepository;

        @Autowired
        private PlayerRepository playerRepository;

        @Autowired
        private RoomRepository roomRepository;

        private MockHttpSession mockHttpSession;
        private ObjectMapper objectMapper = new ObjectMapper();
        private Room mockRoom;
        private Player mockPlayer;
        private List<PlayerGuess> mockGuessList;


        @BeforeEach
        void setUp() {
            this.mockHttpSession = new MockHttpSession();
            mockHttpSession.setAttribute("username", "mockplayer");
            playerScoreRepository.deleteAll();
            roomRepository.deleteAll();
            this.mockPlayer = new Player("mockplayer");
            playerRepository.createPlayer(mockPlayer);
            this.mockGuessList = new ArrayList<>();
            this.mockRoom = new Room("mockRoom", mockPlayer, 4, false, false, "1234", mockGuessList);
        }

        @Test
        void testCreateRoom_NewRoom() throws Exception {
            String roomName = "testroom";
            RoomCreationDTO roomCreationDTO = new RoomCreationDTO(roomName, 4);

            mockMvc.perform(post("/rooms/create-room")
                    .session(mockHttpSession)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(roomCreationDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andExpect(jsonPath("$.room_name", is(roomName)))
                    .andExpect(jsonPath("$.host.username", is("mockplayer")));

            

        }

}
