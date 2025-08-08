package com.jl.mastermind.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.repositories.PlayerScoreRepository;
import com.jl.mastermind.services.PlayerScoreService;

import jakarta.servlet.http.HttpSession;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
public class PlayerScoreServiceTest {
    @Mock
    private PlayerScoreRepository playerScoreRepository;

    @Mock
    private HttpSession session;

    @InjectMocks
    private PlayerScoreService playerScoreService;

    private List<PlayerScore> mockPlayerScores;

    @Test
    void testGetTop10() {
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
        when(playerScoreRepository.findTop10ByDifficultyOrderByScoreDesc(4)).thenReturn(mockPlayerScores);

        List<PlayerScore> foundScores = playerScoreService.getTop10("4");

        assertNotNull(foundScores);
        assertEquals(mockPlayerScores, foundScores);
    }
}
