package com.jl.mastermind.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.jl.mastermind.entities.Player;
import com.jl.mastermind.exceptions.*;
import com.jl.mastermind.repositories.PlayerRepository;
import com.jl.mastermind.services.PlayerScoreService;
import com.jl.mastermind.services.PlayerService;

import jakarta.servlet.http.HttpSession;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
public class PlayerServiceTest {
    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerScoreService playerScoreService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private PlayerService playerService;

    @BeforeEach
    void setUp() {

    }
    

    @Test
    void testGetAllPlayers_Populated() {
        Map<String, Player> mockPlayerMap = new ConcurrentHashMap<>();
        mockPlayerMap.put("testuser1", new Player("testUser1"));
        mockPlayerMap.put("testuser2", new Player("testUser2"));
        mockPlayerMap.put("testuser3", new Player("testUser3"));
        
        when(playerRepository.getPlayerMap()).thenReturn(mockPlayerMap);

        Map<String, Player> foundPlayerMap = playerService.getAllPlayers();

        assertNotNull(foundPlayerMap);
        assertEquals(3, foundPlayerMap.size());
        assertTrue(foundPlayerMap.containsKey("testuser1"));
        assertTrue(foundPlayerMap.containsKey("testuser2"));
        assertTrue(foundPlayerMap.containsKey("testuser3"));

        verify(playerRepository, times(1)).getPlayerMap();
    }

    @Test
    void testGetAllPlayers_Unpopulated() {
        Map<String, Player> mockPlayerMap = new ConcurrentHashMap<>();
        
        when(playerRepository.getPlayerMap()).thenReturn(mockPlayerMap);

        Map<String, Player> foundPlayerMap = playerService.getAllPlayers();

        assertNotNull(foundPlayerMap);
        assertEquals(0, foundPlayerMap.size());
        assertFalse(foundPlayerMap.containsKey("testuser1"));
    }

    @Test
    void testGetPlayerByName_PlayerExists() {
        String username = "testuser";
        Player mockPlayer = new Player(username);
        when(playerRepository.getPlayerByName(username)).thenReturn(Optional.of(mockPlayer));

        Player testPlayer = playerService.getPlayerByName(username);

        assertNotNull(testPlayer);
        assertEquals(username, testPlayer.getUsername());
    }

    @Test
    void testGetPlayerByName_PlayerDoesNotExist() {
        String username = "TESTUSER";
        when(playerRepository.getPlayerByName(username.toLowerCase())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> playerService.getPlayerByName(username));
    }
    
    @Test
    void testDeletePlayer_PlayerExists() {
        String username = "TESTUSER";
        Player mockPlayer = new Player(username);
        when(playerRepository.getPlayerByName(username.toLowerCase())).thenReturn(Optional.of(mockPlayer));
        when(playerRepository.deletePlayer(username.toLowerCase())).thenReturn(true);

        boolean deleted = playerService.deletePlayer(username);

        assertTrue(deleted);

        verify(playerRepository, times(1)).deletePlayer(username.toLowerCase());
    }
    
    @Test
    void testDeletePlayer_PlayerDoesNotExist() {
        String username = "TESTUSER";
        when(playerRepository.getPlayerByName(username.toLowerCase())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> playerService.deletePlayer(username));

        verify(playerRepository, times(0)).deletePlayer(username);
    }
    

    @Test
    void testGetOrCreatePlayer_PlayerExists() {
        String username = "TESTUSER";
        Player mockPlayer = new Player(username);
        when(playerRepository.getPlayerByName(mockPlayer.getUsername().toLowerCase())).thenReturn(Optional.of(mockPlayer));
        
        Player testPlayer = playerService.getOrCreatePlayer(mockPlayer, session);
        
        assertNotNull(testPlayer);
        assertEquals(mockPlayer.getUsername(), testPlayer.getUsername());
    }
    
    @Test
    void testCreatePlayer_PlayerDoesNotExist() {
        String username = "TESTUSER";
        Player mockPlayer = new Player(username);
        when(playerRepository.getPlayerByName(mockPlayer.getUsername().toLowerCase())).thenReturn(Optional.empty());
        when(playerRepository.createPlayer(mockPlayer)).thenReturn(mockPlayer);
        
        Player testPlayer = playerService.getOrCreatePlayer(mockPlayer, session);
        
        assertNotNull(testPlayer);
        assertEquals(mockPlayer, testPlayer);
    
        verify(playerRepository, times(1)).createPlayer(mockPlayer);
    }
}
