package com.jl.mastermind.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.repositories.PlayerScoreRepository;
import com.jl.mastermind.services.PlayerScoreService;

import jakarta.servlet.http.HttpSession;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PlayerScoreServiceTest {
    @Mock
    private PlayerScoreRepository playerScoreRepository;

    @Mock
    private HttpSession session;

    @InjectMocks
    private PlayerScoreService playerScoreService;

    @Test
    void testGetTop10() {
        List<PlayerScore> mockPlayerScores = Arrays.asList(
            new PlayerScore("Player1", 1000, 4),
            new PlayerScore("Player2", 950, 4),
            new PlayerScore("Player3", 900, 4),
            new PlayerScore("Player4", 850, 4),
            new PlayerScore("Player5", 800, 4)
        );
        when(playerScoreRepository.findTop10ByDifficultyOrderByScoreDesc(4)).thenReturn(mockPlayerScores);

        List<PlayerScore> foundScores = playerScoreService.getTop10("4");

        assertNotNull(foundScores);
        assertEquals(mockPlayerScores, foundScores);
    }
}
