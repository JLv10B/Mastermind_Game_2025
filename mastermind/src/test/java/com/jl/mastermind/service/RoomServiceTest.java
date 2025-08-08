package com.jl.mastermind.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.jl.mastermind.dto.PlayerRoomViewDTO;
import com.jl.mastermind.dto.RoomCreationDTO;
import com.jl.mastermind.dto.RoomUpdateDTO;
import com.jl.mastermind.entities.Player;
import com.jl.mastermind.entities.PlayerGuess;
import com.jl.mastermind.entities.Room;
import com.jl.mastermind.exceptions.*;
import com.jl.mastermind.repositories.RoomRepository;
import com.jl.mastermind.services.PlayerService;
import com.jl.mastermind.services.RoomService;

import jakarta.servlet.http.HttpSession;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
public class RoomServiceTest {

    Map<String, Room> mockRoomMap;
    Player mockPlayer1;
    Player mockPlayer2;
    Player mockPlayer3;
    Room mockRoom1;
    Room mockRoom2;
    Map<String,List<PlayerGuess>> participants;
    List<PlayerGuess> guessList;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HttpSession session;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setupTestData() {
        this.mockRoomMap = new ConcurrentHashMap<>();
        this.mockPlayer1 = new Player("PLAYER1");
        this.mockPlayer2 = new Player("PLAYER2");
        this.mockPlayer3 = new Player("PLAYER3");
        this.participants = new ConcurrentHashMap<>();
        this.guessList = new ArrayList<>();
        this.mockRoom1 = new Room("ROOM1", mockPlayer1, 4, false, false, "1234", guessList);
        this.mockRoom2 = new Room("ROOM2", mockPlayer2, 4, false, false, "1234", guessList);
        mockRoomMap.put(mockRoom1.getRoomName().toLowerCase(), mockRoom1);
        mockRoomMap.put(mockRoom2.getRoomName().toLowerCase(), mockRoom2);
    }

    @Test
    void testGetRoomMap_Populated() {
        when(roomRepository.getRoomMap()).thenReturn(mockRoomMap);

        Map<String, Room> testRoomMap = roomService.getRoomMap();

        assertNotNull(testRoomMap);
        assertEquals(2, testRoomMap.size());
        assertTrue(testRoomMap.containsKey("room1"));
        assertTrue(testRoomMap.containsKey("room2"));

        verify(roomRepository, times(1)).getRoomMap();
    }

    @Test
    void testGetRoom_Exists() {
        String roomName = "ROOM1";
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));

        Room testRoom = roomService.getRoom(roomName);

        assertNotNull(testRoom);
        assertEquals(testRoom, mockRoom1);
        assertEquals(roomName, testRoom.getRoomName());
    }

    @Test
    void testGetRoom_DoesNotExist() {
        String roomName = "ROOM10";
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoom(roomName));
    }

    @Test
    void testGetRoomPublic_Exists() {
        String roomName = mockRoom1.getRoomName();
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));
        when(session.getAttribute("username")).thenReturn(mockPlayer1.getUsername());

        PlayerRoomViewDTO testRoomView = roomService.getRoomPublic(roomName, session);

        assertNotNull(testRoomView);
        assertEquals(testRoomView.getRoomName(), mockRoom1.getRoomName());
        assertEquals(testRoomView.getHost(), mockRoom1.getHost());
        assertEquals(testRoomView.getDifficulty(), mockRoom1.getDifficulty());
        assertEquals(testRoomView.isStarted(), mockRoom1.isStarted());
    }

    @Test
    void testGetRoomPublic_NoPerms() {
        String username = "Todd";
        String roomName = mockRoom1.getRoomName();
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));
        when(session.getAttribute("username")).thenReturn(username);

        assertThrows(InsufficientPermissionsException.class, () -> roomService.getRoomPublic(roomName, session));
    }

    @Test
    void testGetRoomPublic_DoesNotExist() {
        session.setAttribute("username", mockPlayer1.getUsername());
        String roomName = "nonexistantRoom";
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoomPublic(roomName, session));
    }
 
    @Test
    void testDeleteRoom_Exists() {
        String roomName = mockRoom1.getRoomName();
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));
        when(session.getAttribute("username")).thenReturn(mockPlayer1.getUsername());
        when(roomRepository.deleteRoom(roomName.toLowerCase())).thenReturn(true);

        boolean deleted =roomService.deleteRoom(roomName, session);

        assertTrue(deleted);

        verify(roomRepository, times(1)).deleteRoom(roomName.toLowerCase());
    }
 
    @Test
    void testDeleteRoom_NotHostUser() {
        String roomName = mockRoom1.getRoomName();
        String username = "Todd";
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));
        when(session.getAttribute("username")).thenReturn(username);

        assertThrows(InsufficientPermissionsException.class, () -> roomService.deleteRoom(roomName, session));
    }
 
    @Test
    void testDeleteRoom_DoesNotExist() {
        String roomName = mockRoom1.getRoomName();
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.deleteRoom(roomName, session));
    }

    @Test
    void testUpdateRoom_RoomStarted() throws URISyntaxException {
        String roomName = mockRoom1.getRoomName();
        mockRoom1.setStarted(true);
        RoomUpdateDTO roomUpdateDTO = new RoomUpdateDTO(6, null, null);
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));

        assertThrows(InsufficientPermissionsException.class, () -> roomService.updateRoom(roomName, roomUpdateDTO));
    }

    @Test
    void testUpdateRoom_DifficultyValid() throws URISyntaxException {
        String roomName = mockRoom1.getRoomName();
        RoomUpdateDTO roomUpdateDTO = new RoomUpdateDTO(6, null, null);
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));
        when(roomRepository.saveRoom(mockRoom1)).thenReturn(mockRoom1);

        Room updatedRoom = roomService.updateRoom(roomName, roomUpdateDTO);

        assertNotNull(updatedRoom);
        assertEquals(6, updatedRoom.getDifficulty());
        assertEquals(false, updatedRoom.isStarted());
        assertEquals("1234", updatedRoom.getMastercode());
        verify(roomRepository, times(1)).saveRoom(updatedRoom);
    }

    @Test
    void testUpdateRoom_DifficultyTooHigh() throws URISyntaxException {
        String roomName = mockRoom1.getRoomName();
        RoomUpdateDTO roomUpdateDTO = new RoomUpdateDTO(8, null, null);
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));
        
        assertThrows(InvalidInputException.class, () -> roomService.updateRoom(roomName, roomUpdateDTO));
    }

    @Test
    void testUpdateRoom_DifficultyTooLow() throws URISyntaxException {
        String roomName = mockRoom1.getRoomName();
        RoomUpdateDTO roomUpdateDTO = new RoomUpdateDTO(2, null, null);
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));
        
        assertThrows(InvalidInputException.class, () -> roomService.updateRoom(roomName, roomUpdateDTO));
    }

    @Test
    void testUpdateRoom_setStartedValid() throws URISyntaxException {
        String roomName = mockRoom1.getRoomName();
        RoomUpdateDTO roomUpdateDTO = new RoomUpdateDTO(null, true, null);
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));
        when(roomRepository.saveRoom(mockRoom1)).thenReturn(mockRoom1);

        Room updatedRoom = roomService.updateRoom(roomName, roomUpdateDTO);

        assertNotNull(updatedRoom);
        assertEquals(4, updatedRoom.getDifficulty());

        assertEquals(true, updatedRoom.isStarted());
        assertEquals("1234", updatedRoom.getMastercode());
        verify(roomRepository, times(1)).saveRoom(updatedRoom);
    }

    @Test
    void testResetRoom_Valid() throws URISyntaxException {
        String roomName = mockRoom1.getRoomName();
        String roomHost = mockRoom1.getHost().getUsername();
        RoomUpdateDTO roomUpdateDTO = new RoomUpdateDTO(null, null, true);
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));
        when(session.getAttribute("username")).thenReturn(roomHost);
        when(roomRepository.saveRoom(mockRoom1)).thenReturn(mockRoom1);

        PlayerRoomViewDTO updatedRoomView = roomService.resetRoom(roomName, roomUpdateDTO, session);

        assertNotNull(updatedRoomView);
        assertEquals(false, updatedRoomView.isStarted());
        assertNotEquals("1234", mockRoom1.getMastercode());
        verify(roomRepository, times(1)).saveRoom(mockRoom1);
    }

    @Test
    void testResetRoom_InvalidUser() throws URISyntaxException {
        String roomName = mockRoom1.getRoomName();
        String roomHost = "Todd";
        RoomUpdateDTO roomUpdateDTO = new RoomUpdateDTO(null, null, true);
        when(roomRepository.findByRoomName(roomName.toLowerCase())).thenReturn(Optional.of(mockRoom1));
        when(session.getAttribute("username")).thenReturn(roomHost);

        assertThrows(InsufficientPermissionsException.class, () -> roomService.resetRoom(roomName, roomUpdateDTO, session));
    }

    @Test
    void testCreateRoom_Valid() throws URISyntaxException {
        Player testPlayer = new Player("Todd");
        RoomCreationDTO testRoomCreationDTO = new RoomCreationDTO("testRoom", 4);
        when(session.getAttribute("username")).thenReturn("Todd");
        when(roomRepository.findByRoomName(testRoomCreationDTO.getRoomName().toLowerCase())).thenReturn(Optional.empty());
        when(playerService.getPlayerByName(testPlayer.getUsername().toLowerCase())).thenReturn(testPlayer);
        
        Room createdTestRoom = roomService.getOrCreateRoom(testRoomCreationDTO, session);

        assertNotNull(createdTestRoom);
        assertEquals(4, createdTestRoom.getDifficulty());
        assertEquals(false, createdTestRoom.isStarted());
        verify(roomRepository, times(1)).saveRoom(createdTestRoom);
    }

    @Test
    void testCreateRoom_RoomExistsValidPerms() throws URISyntaxException {
        RoomCreationDTO testRoomCreationDTO = new RoomCreationDTO("ROOM1", 4);
        when(session.getAttribute("username")).thenReturn(mockPlayer1.getUsername());
        when(roomRepository.findByRoomName(testRoomCreationDTO.getRoomName().toLowerCase())).thenReturn(Optional.of(mockRoom1));

        Room createdTestRoom = roomService.getOrCreateRoom(testRoomCreationDTO, session);

        assertNotNull(createdTestRoom);
        assertEquals(4, createdTestRoom.getDifficulty());
        assertEquals("ROOM1", createdTestRoom.getRoomName());
    }

    @Test
    void testCreateRoom_RoomExistsInvalidPerms() throws URISyntaxException {
        String username = "Todd";
        RoomCreationDTO testRoomCreationDTO = new RoomCreationDTO("ROOM1", 4);
        when(session.getAttribute("username")).thenReturn(username);
        when(roomRepository.findByRoomName(testRoomCreationDTO.getRoomName().toLowerCase())).thenReturn(Optional.of(mockRoom1));

        assertThrows(InsufficientPermissionsException.class, () -> roomService.getOrCreateRoom(testRoomCreationDTO, session));
    }

    @Test
    void testCreateRoom_NotLoggedIn() throws URISyntaxException {
        RoomCreationDTO testRoomCreationDTO = new RoomCreationDTO("ROOM1", 4);
        when(session.getAttribute("username")).thenReturn(null);
        
        assertThrows(NoUserFoundException.class, () -> roomService.getOrCreateRoom(testRoomCreationDTO, session));
    }

    //TODO: submit guess, create guess, randompatterngenerator
}
