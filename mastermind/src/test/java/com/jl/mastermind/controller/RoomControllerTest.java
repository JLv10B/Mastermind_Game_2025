package com.jl.mastermind.controller;

import static com.jl.mastermind.util.AppConstants.RoomParameters.MAX_GUESSES;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jl.mastermind.controllers.RoomController;
import com.jl.mastermind.dto.PlayerGuessDTO;
import com.jl.mastermind.dto.PlayerRoomViewDTO;
import com.jl.mastermind.dto.RoomCreationDTO;
import com.jl.mastermind.dto.RoomUpdateDTO;
import com.jl.mastermind.entities.Player;
import com.jl.mastermind.entities.PlayerGuess;
import com.jl.mastermind.entities.Room;
import com.jl.mastermind.services.RoomService;


@WebMvcTest(RoomController.class)
@ActiveProfiles("dev")
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    private MockHttpSession mockHttpSession = new MockHttpSession();
    private Player mockPlayer = new Player("testuser");
    private List<PlayerGuess> guessList = new ArrayList<>();
    private String roomName = "testroom";
    private ObjectMapper objectMapper = new ObjectMapper();
    private PlayerRoomViewDTO playerRoomViewDTO = new PlayerRoomViewDTO("testroom", mockPlayer, 4, false, false, MAX_GUESSES, guessList);

    @Test
    void testGetRoom_PublicView() throws Exception {
        when(roomService.getRoomPublic(roomName.toLowerCase(), mockHttpSession)).thenReturn(playerRoomViewDTO);

        mockMvc.perform(get("/rooms/{roomName}", roomName)
                .session(mockHttpSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.room_name", is("testroom")))
                .andExpect(jsonPath("$.mastercode").doesNotExist());
    }

    @Test
    void testDeleteRoom_Exists() throws Exception {
        when(roomService.deleteRoom(roomName, mockHttpSession) == true).thenReturn(true);

        mockMvc.perform(delete("/rooms/{roomName}", roomName)
                .session(mockHttpSession))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteRoom_DoesNotExist() throws Exception {
        when(roomService.deleteRoom(roomName, mockHttpSession) == true).thenReturn(false);

        mockMvc.perform(delete("/rooms/{roomName}", roomName)
                .session(mockHttpSession))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateRoom() throws Exception {
        RoomUpdateDTO roomUpdate = new RoomUpdateDTO(5, false, false);
        Room updatedRoom = new Room("testroom", mockPlayer, 5, false, false, "1234", guessList);
        when(roomService.updateRoom(roomName, roomUpdate)).thenReturn(updatedRoom);

        mockMvc.perform(patch("/rooms/{roomName}", roomName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomUpdate)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateRoom() throws Exception {
        RoomCreationDTO roomCreationDTO = new RoomCreationDTO(roomName, 4);
        Room createdRoom = new Room(roomName, mockPlayer, 4, false, false, "1234", guessList);
        when(roomService.getOrCreateRoom(roomCreationDTO, mockHttpSession)).thenReturn(createdRoom);

        mockMvc.perform(post("/rooms/create-room")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomCreationDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void testSubmitGuess() throws Exception {
        PlayerGuessDTO playerGuessDTO = new PlayerGuessDTO("1234");
        PlayerGuess playerGuess = new PlayerGuess(roomName, false, "1234", 9);
        when(roomService.submitGuess(roomName, playerGuessDTO, mockHttpSession)).thenReturn(playerGuess);

        mockMvc.perform(post("/rooms/{roomName}/submit-guess", roomName)
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(playerGuessDTO)))
                .andDo(print())
                .andExpect(status().isCreated());
                
    }
}
